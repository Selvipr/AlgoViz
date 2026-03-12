package com.example.algoviz.domain.engine

data class ArenaProblem(
    val id: String,
    val title: String,
    val difficulty: String,
    val topics: List<String>,
    val description: String,
    val initialCode: String,
    val testCases: List<TestCase>
)

data class TestCase(
    val functionCall: String,
    val expectedOutput: String,
    val isHidden: Boolean = false
)

object ArenaDataProvider {
    val problems = listOf(
        ArenaProblem(
            id = "two_sum",
            title = "Two Sum",
            difficulty = "Easy",
            topics = listOf("Array", "Hash Table"),
            description = "Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.",
            initialCode = """
public int[] twoSum(int[] nums, int target) {
    // Write your solution here
    return new int[]{};
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("twoSum(new int[]{2, 7, 11, 15}, 9)", "new int[]{0, 1}"),
                TestCase("twoSum(new int[]{3, 2, 4}, 6)", "new int[]{1, 2}"),
                TestCase("twoSum(new int[]{3, 3}, 6)", "new int[]{0, 1}", true)
            )
        ),
        ArenaProblem(
            id = "valid_parentheses",
            title = "Valid Parentheses",
            difficulty = "Easy",
            topics = listOf("String", "Stack"),
            description = "Given a string `s` containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.",
            initialCode = """
public boolean isValid(String s) {
    // Write your solution here
    return false;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("isValid(\"()\")", "true"),
                TestCase("isValid(\"()[]{}\")", "true"),
                TestCase("isValid(\"(]\")", "false"),
                TestCase("isValid(\"{[]}\")", "true", true)
            )
        ),
        ArenaProblem(
            id = "binary_search",
            title = "Binary Search",
            difficulty = "Easy",
            topics = listOf("Array", "Binary Search"),
            description = "Given an array of integers `nums` which is sorted in ascending order, and an integer `target`, write a function to search `target` in `nums`. If `target` exists, then return its index. Otherwise, return `-1`.",
            initialCode = """
public int search(int[] nums, int target) {
    // Write your solution here
    return -1;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("search(new int[]{-1, 0, 3, 5, 9, 12}, 9)", "4"),
                TestCase("search(new int[]{-1, 0, 3, 5, 9, 12}, 2)", "-1"),
                TestCase("search(new int[]{5}, 5)", "0", true)
            )
        ),
        ArenaProblem(
            id = "bubble_sort",
            title = "Bubble Sort",
            difficulty = "Medium",
            topics = listOf("Array", "Sorting"),
            description = "Given an array of integers `nums`, sort the array in ascending order using the Bubble Sort algorithm and return the sorted array.",
            initialCode = """
public int[] sortArray(int[] nums) {
    // Write your solution here
    return nums;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("sortArray(new int[]{5, 2, 3, 1})", "new int[]{1, 2, 3, 5}"),
                TestCase("sortArray(new int[]{5, 1, 1, 2, 0, 0})", "new int[]{0, 0, 1, 1, 2, 5}"),
                TestCase("sortArray(new int[]{})", "new int[]{}", true)
            )
        ),
        ArenaProblem(
            id = "reverse_string",
            title = "Reverse String",
            difficulty = "Easy",
            topics = listOf("String", "Two Pointers"),
            description = "A function that reverses a string. Create a new reversed string and return it.",
            initialCode = """
public String reverseString(String s) {
    // Write your solution here
    return "";
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("reverseString(\"hello\")", "\"olleh\""),
                TestCase("reverseString(\"Hannah\")", "\"hannaH\""),
                TestCase("reverseString(\"a\")", "\"a\"", true)
            )
        ),
        ArenaProblem(
            id = "contains_duplicate",
            title = "Contains Duplicate",
            difficulty = "Easy",
            topics = listOf("Array", "Hash Table"),
            description = "Given an integer array `nums`, return `true` if any value appears at least twice in the array, and return `false` if every element is distinct.",
            initialCode = """
public boolean containsDuplicate(int[] nums) {
    // Write your solution here
    return false;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("containsDuplicate(new int[]{1, 2, 3, 1})", "true"),
                TestCase("containsDuplicate(new int[]{1, 2, 3, 4})", "false"),
                TestCase("containsDuplicate(new int[]{1, 1, 1, 3, 3, 4, 3, 2, 4, 2})", "true", true)
            )
        ),
        ArenaProblem(
            id = "single_number",
            title = "Single Number",
            difficulty = "Easy",
            topics = listOf("Array", "Bit Manipulation"),
            description = "Given a non-empty array of integers `nums`, every element appears twice except for one. Find that single one.",
            initialCode = """
public int singleNumber(int[] nums) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("singleNumber(new int[]{2, 2, 1})", "1"),
                TestCase("singleNumber(new int[]{4, 1, 2, 1, 2})", "4"),
                TestCase("singleNumber(new int[]{1})", "1", true)
            )
        ),
        ArenaProblem(
            id = "maximum_subarray",
            title = "Maximum Subarray",
            difficulty = "Medium",
            topics = listOf("Array", "Dynamic Programming"),
            description = "Given an integer array `nums`, find the contiguous subarray which has the largest sum and return its sum.",
            initialCode = """
public int maxSubArray(int[] nums) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4})", "6"),
                TestCase("maxSubArray(new int[]{1})", "1"),
                TestCase("maxSubArray(new int[]{5, 4, -1, 7, 8})", "23", true)
            )
        ),
        ArenaProblem(
            id = "climbing_stairs",
            title = "Climbing Stairs",
            difficulty = "Easy",
            topics = listOf("Math", "Dynamic Programming"),
            description = "You are climbing a staircase. It takes `n` steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?",
            initialCode = """
public int climbStairs(int n) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("climbStairs(2)", "2"),
                TestCase("climbStairs(3)", "3"),
                TestCase("climbStairs(4)", "5", true)
            )
        ),
        ArenaProblem(
            id = "valid_palindrome",
            title = "Valid Palindrome",
            difficulty = "Easy",
            topics = listOf("String", "Two Pointers"),
            description = "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.",
            initialCode = """
public boolean isPalindrome(String s) {
    // Write your solution here
    return false;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("isPalindrome(\"A man, a plan, a canal: Panama\")", "true"),
                TestCase("isPalindrome(\"race a car\")", "false"),
                TestCase("isPalindrome(\" \")", "true", true)
            )
        ),
        ArenaProblem(
            id = "missing_number",
            title = "Missing Number",
            difficulty = "Easy",
            topics = listOf("Array", "Math", "Bit Manipulation"),
            description = "Given an array `nums` containing `n` distinct numbers in the range `[0, n]`, return the only number in the range that is missing from the array.",
            initialCode = """
public int missingNumber(int[] nums) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("missingNumber(new int[]{3, 0, 1})", "2"),
                TestCase("missingNumber(new int[]{0, 1})", "2"),
                TestCase("missingNumber(new int[]{9,6,4,2,3,5,7,0,1})", "8", true)
            )
        ),
        ArenaProblem(
            id = "move_zeroes",
            title = "Move Zeroes",
            difficulty = "Easy",
            topics = listOf("Array", "Two Pointers"),
            description = "Given an integer array `nums`, move all `0`'s to the end of it while maintaining the relative order of the non-zero elements. Return the modified array.",
            initialCode = """
public int[] moveZeroes(int[] nums) {
    // Modify and return nums
    return nums;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("moveZeroes(new int[]{0, 1, 0, 3, 12})", "new int[]{1, 3, 12, 0, 0}"),
                TestCase("moveZeroes(new int[]{0})", "new int[]{0}"),
                TestCase("moveZeroes(new int[]{1, 0})", "new int[]{1, 0}", true)
            )
        ),
        ArenaProblem(
            id = "fibonacci",
            title = "Fibonacci Number",
            difficulty = "Easy",
            topics = listOf("Math", "Dynamic Programming"),
            description = "The Fibonacci numbers, commonly denoted `F(n)` form a sequence, such that each number is the sum of the two preceding ones, starting from `0` and `1`.",
            initialCode = """
public int fib(int n) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("fib(2)", "1"),
                TestCase("fib(3)", "2"),
                TestCase("fib(4)", "3", true)
            )
        ),
        ArenaProblem(
            id = "search_insert_position",
            title = "Search Insert Position",
            difficulty = "Easy",
            topics = listOf("Array", "Binary Search"),
            description = "Given a sorted array of distinct integers and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order.",
            initialCode = """
public int searchInsert(int[] nums, int target) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("searchInsert(new int[]{1, 3, 5, 6}, 5)", "2"),
                TestCase("searchInsert(new int[]{1, 3, 5, 6}, 2)", "1"),
                TestCase("searchInsert(new int[]{1, 3, 5, 6}, 7)", "4", true)
            )
        ),
        ArenaProblem(
            id = "squares_of_a_sorted_array",
            title = "Squares of a Sorted Array",
            difficulty = "Easy",
            topics = listOf("Array", "Two Pointers"),
            description = "Given an integer array `nums` sorted in non-decreasing order, return an array of the squares of each number sorted in non-decreasing order.",
            initialCode = """
public int[] sortedSquares(int[] nums) {
    // Write your solution here
    return new int[]{};
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("sortedSquares(new int[]{-4, -1, 0, 3, 10})", "new int[]{0, 1, 9, 16, 100}"),
                TestCase("sortedSquares(new int[]{-7, -3, 2, 3, 11})", "new int[]{4, 9, 9, 49, 121}"),
                TestCase("sortedSquares(new int[]{0})", "new int[]{0}", true)
            )
        ),
        ArenaProblem(
            id = "find_min_rotated",
            title = "Find Minimum in Rotated Sorted Array",
            difficulty = "Medium",
            topics = listOf("Array", "Binary Search"),
            description = "Suppose an array of length `n` sorted in ascending order is rotated between `1` and `n` times. Return the minimum element of this array. Write an algorithm that runs in O(log n) time.",
            initialCode = """
public int findMin(int[] nums) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("findMin(new int[]{3, 4, 5, 1, 2})", "1"),
                TestCase("findMin(new int[]{4, 5, 6, 7, 0, 1, 2})", "0"),
                TestCase("findMin(new int[]{11, 13, 15, 17})", "11", true)
            )
        ),
        ArenaProblem(
            id = "valid_anagram",
            title = "Valid Anagram",
            difficulty = "Easy",
            topics = listOf("Hash Table", "String"),
            description = "Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise.",
            initialCode = """
public boolean isAnagram(String s, String t) {
    // Write your solution here
    return false;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("isAnagram(\"anagram\", \"nagaram\")", "true"),
                TestCase("isAnagram(\"rat\", \"car\")", "false"),
                TestCase("isAnagram(\"a\", \"b\")", "false", true)
            )
        ),
        ArenaProblem(
            id = "majority_element",
            title = "Majority Element",
            difficulty = "Easy",
            topics = listOf("Array", "Hash Table"),
            description = "Given an array `nums` of size `n`, return the majority element. The majority element is the element that appears more than `n / 2` times.",
            initialCode = """
public int majorityElement(int[] nums) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("majorityElement(new int[]{3, 2, 3})", "3"),
                TestCase("majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2})", "2"),
                TestCase("majorityElement(new int[]{1})", "1", true)
            )
        ),
        ArenaProblem(
            id = "buy_and_sell_stock",
            title = "Best Time to Buy and Sell Stock",
            difficulty = "Easy",
            topics = listOf("Array", "Dynamic Programming"),
            description = "You are given an array `prices` where `prices[i]` is the price of a given stock on the `i`th day. Maximize your profit by choosing a single day to buy one stock and a different day to sell. Return the max profit.",
            initialCode = """
public int maxProfit(int[] prices) {
    // Write your solution here
    return 0;
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("maxProfit(new int[]{7, 1, 5, 3, 6, 4})", "5"),
                TestCase("maxProfit(new int[]{7, 6, 4, 3, 1})", "0"),
                TestCase("maxProfit(new int[]{1, 2})", "1", true)
            )
        ),
        ArenaProblem(
            id = "product_except_self",
            title = "Product of Array Except Self",
            difficulty = "Medium",
            topics = listOf("Array", "Prefix Sum"),
            description = "Given an integer array `nums`, return an array `answer` such that `answer[i]` is equal to the product of all the elements of `nums` except `nums[i]`.",
            initialCode = """
public int[] productExceptSelf(int[] nums) {
    // Write your solution here
    return new int[]{};
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("productExceptSelf(new int[]{1, 2, 3, 4})", "new int[]{24, 12, 8, 6}"),
                TestCase("productExceptSelf(new int[]{-1, 1, 0, -3, 3})", "new int[]{0, 0, 9, 0, 0}"),
                TestCase("productExceptSelf(new int[]{0, 0})", "new int[]{0, 0}", true)
            )
        ),
        ArenaProblem(
            id = "find_disappeared_numbers",
            title = "Find Disappeared Numbers",
            difficulty = "Easy",
            topics = listOf("Array", "Hash Table"),
            description = "Given an array `nums` of `n` integers where `nums[i]` is in the range `[1, n]`, return an array of all the integers in the range `[1, n]` that do not appear in `nums`.",
            initialCode = """
public int[] findDisappearedNumbers(int[] nums) {
    // Write your solution here
    return new int[]{};
}
            """.trimIndent(),
            testCases = listOf(
                TestCase("findDisappearedNumbers(new int[]{4, 3, 2, 7, 8, 2, 3, 1})", "new int[]{5, 6}"),
                TestCase("findDisappearedNumbers(new int[]{1, 1})", "new int[]{2}"),
                TestCase("findDisappearedNumbers(new int[]{2, 2})", "new int[]{1}", true)
            )
        )
    )
}
