package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.TreeNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep

class BSTInsertVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val elementsToInsert = initialData as? List<Int>
            ?: throw IllegalArgumentException("BST Insert expects List<Int>")

        val steps = mutableListOf<VisualizationStep>()
        var root: TreeNode? = null
        var currentId = 0

        // Start empty step
        steps.add(
            VisualizationStep(
                state = VisualizationState.TreeState(root = null),
                explanation = "Initializing an empty Binary Search Tree."
            )
        )

        for (value in elementsToInsert) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.TreeState(root = cloneTree(root)),
                    explanation = "Preparing to insert value: $value.",
                    activeLine = 1
                )
            )

            if (root == null) {
                root = TreeNode(id = currentId++, value = value)
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.TreeState(
                            root = cloneTree(root),
                            foundNode = root.id
                        ),
                        explanation = "Tree is empty. Inserting $value as the root node.",
                        activeLine = 3
                    )
                )
                continue
            }

            var curr: TreeNode = root
            while (true) {
                // Highlight the comparison
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.TreeState(
                            root = cloneTree(root),
                            comparingNode = curr.id
                        ),
                        explanation = "Comparing $value to ${curr.value}.",
                        activeLine = 4
                    )
                )

                if (value < curr.value) {
                    if (curr.left == null) {
                        steps.add(
                            VisualizationStep(
                                state = VisualizationState.TreeState(
                                    root = cloneTree(root),
                                    comparingNode = curr.id
                                ),
                                explanation = "$value is less than ${curr.value}, and the left child is empty. Inserting here.",
                                activeLine = 5
                            )
                        )
                        val newNode = TreeNode(id = currentId++, value = value)
                        curr.left = newNode
                        
                        steps.add(
                            VisualizationStep(
                                state = VisualizationState.TreeState(
                                    root = cloneTree(root),
                                    foundNode = newNode.id
                                ),
                                explanation = "Successfully inserted $value into the BST."
                            )
                        )
                        break
                    } else {
                        steps.add(
                            VisualizationStep(
                                state = VisualizationState.TreeState(
                                    root = cloneTree(root),
                                    comparingNode = curr.id
                                ),
                                explanation = "$value is less than ${curr.value}. Traversing down the left subtree.",
                                activeLine = 5
                            )
                        )
                        curr = curr.left!!
                    }
                } else if (value > curr.value) {
                    if (curr.right == null) {
                        steps.add(
                            VisualizationStep(
                                state = VisualizationState.TreeState(
                                    root = cloneTree(root),
                                    comparingNode = curr.id
                                ),
                                explanation = "$value is greater than ${curr.value}, and the right child is empty. Inserting here.",
                                activeLine = 7
                            )
                        )
                        val newNode = TreeNode(id = currentId++, value = value)
                        curr.right = newNode

                        steps.add(
                            VisualizationStep(
                                state = VisualizationState.TreeState(
                                    root = cloneTree(root),
                                    foundNode = newNode.id
                                ),
                                explanation = "Successfully inserted $value into the BST."
                            )
                        )
                        break
                    } else {
                        steps.add(
                            VisualizationStep(
                                state = VisualizationState.TreeState(
                                    root = cloneTree(root),
                                    comparingNode = curr.id
                                ),
                                explanation = "$value is greater than ${curr.value}. Traversing down the right subtree.",
                                activeLine = 7
                            )
                        )
                        curr = curr.right!!
                    }
                } else {
                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.TreeState(
                                root = cloneTree(root),
                                comparingNode = curr.id,
                                foundNode = curr.id
                            ),
                            explanation = "$value is equal to ${curr.value}. Duplicate values are not inserted."
                        )
                    )
                    break
                }
            }
        }

        steps.add(
            VisualizationStep(
                state = VisualizationState.TreeState(root = cloneTree(root)),
                explanation = "All elements have been inserted. The Binary Search Tree is fully populated."
            )
        )

        return steps
    }

    // Helper to deeply clone the tree structure for immutable state snapshots
    private fun cloneTree(node: TreeNode?): TreeNode? {
        if (node == null) return null
        return TreeNode(
            id = node.id,
            value = node.value,
            left = cloneTree(node.left),
            right = cloneTree(node.right)
        )
    }
}
