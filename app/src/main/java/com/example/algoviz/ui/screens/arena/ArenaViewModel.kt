package com.example.algoviz.ui.screens.arena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsh.Interpreter
import com.example.algoviz.domain.engine.ArenaDataProvider
import com.example.algoviz.domain.engine.ArenaProblem
import com.example.algoviz.domain.engine.TestCase
import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TestCaseResult(
    val testCase: TestCase,
    val passed: Boolean,
    val actualOutput: String?,
    val errorMessage: String? = null
)

@HiltViewModel
class ArenaViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentProblem = MutableStateFlow<ArenaProblem?>(null)
    val currentProblem: StateFlow<ArenaProblem?> = _currentProblem.asStateFlow()

    private val _userCode = MutableStateFlow("")
    val userCode: StateFlow<String> = _userCode.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    private val _executionResults = MutableStateFlow<List<TestCaseResult>>(emptyList())
    val executionResults: StateFlow<List<TestCaseResult>> = _executionResults.asStateFlow()

    fun loadProblem(problemId: String) {
        val problem = ArenaDataProvider.problems.find { it.id == problemId }
        _currentProblem.value = problem
        _userCode.value = problem?.initialCode ?: ""
        _executionResults.value = emptyList()
    }

    fun updateCode(code: String) {
        _userCode.value = code
    }

    fun executeCode() {
        val problem = _currentProblem.value ?: return
        val codeToRun = _userCode.value

        _isExecuting.value = true
        _executionResults.value = emptyList()

        viewModelScope.launch {
            val results = withContext(Dispatchers.Default) {
                problem.testCases.map { testCase ->
                    runTestCase(codeToRun, testCase)
                }
            }
            
            // Check if all test cases passed
            val allPassed = results.isNotEmpty() && results.all { it.passed }
            if (allPassed) {
                try {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        userRepository.addXp(user.id, 20) // Award 20 XP for a successful solve!
                        userRepository.updateStreak(user.id) // Update activity streak
                    }
                } catch (e: Exception) {
                    // Ignore XP tracking failure if offline
                }
            }

            _executionResults.value = results
            _isExecuting.value = false
        }
    }

    private fun runTestCase(userCode: String, testCase: TestCase): TestCaseResult {
        return try {
            val interpreter = Interpreter()
            
            // 1. Evaluate user's code snippet so it exists in BeanShell context
            interpreter.eval(userCode)
            
            // 2. Evaluate the test case's expected output so we can compare native types
            val expectedScript = "expectedVal = ${testCase.expectedOutput};"
            interpreter.eval(expectedScript)
            val expectedValue = interpreter.get("expectedVal")

            // 3. Evaluate the actual function call
            val executionScript = "actualVal = ${testCase.functionCall};"
            interpreter.eval(executionScript)
            val actualValue = interpreter.get("actualVal")

            // Compare arrays specially
            val passed = if (expectedValue != null && expectedValue.javaClass.isArray && actualValue != null && actualValue.javaClass.isArray) {
                compareArrays(expectedValue, actualValue)
            } else {
                expectedValue == actualValue
            }

            TestCaseResult(
                testCase = testCase,
                passed = passed,
                actualOutput = arrayToString(actualValue),
                errorMessage = null
            )

        } catch (e: Exception) {
            TestCaseResult(
                testCase = testCase,
                passed = false,
                actualOutput = null,
                errorMessage = e.message ?: "Execution Error"
            )
        }
    }
    
    // Helper to deeply compare arrays natively
    private fun compareArrays(arr1: Any, arr2: Any): Boolean {
        if (arr1 is IntArray && arr2 is IntArray) return arr1.contentEquals(arr2)
        if (arr1 is Array<*> && arr2 is Array<*>) return arr1.contentEquals(arr2)
        // Add more primitive array comparisons as needed
        return arr1 == arr2
    }
    
    private fun arrayToString(obj: Any?): String {
        if (obj == null) return "null"
        if (obj is IntArray) return obj.contentToString()
        if (obj is Array<*>) return obj.contentToString()
        return obj.toString()
    }
}
