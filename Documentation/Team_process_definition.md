## Work flow
  This section outlines the lifecycle of a task.

1. Task is picked when it meets definition of ready
2. A branch is created by the assigned person (e.g. feature/resume-upload)
3. Completed work is commited, and pushed into the created branch
4. Pull Request is called, includig summary of changes
5. Team member reviews and suggests changes if required
6. After approval, the branch is merged into main
7. Branch is deleted, and task is marked as Done

## Branching Strategy
  A new branch is created for each task ,with a descriptive name (eg feature/resume-upload). No team memeber pushes directly to the main. The person working on the branch opens a pull request.Once their work is reviewed by another team member and approved, it is now ready to merge into main.This ensures the stability of the main.

## Pull Request process
  Proposes merging changes from one branch into another.It is where the team reviews a code before shipping into main.

1. Create a branch from  main
2. Commit work with descriptive message
3. Push the created branch and open a Pull Request with summary of changes
4. Request review from teammate
5. Merge into main after approval
6. Delete the branch after merging

## Code review process
  Reviewing of the code allows to catch bugs before they reach the main.An assigned team member reviews a pull request and verifies the following before aproval of the merge.

1. The code must work as intended
2. It must repect the acceptance criteria
3. It is readable and easy to understand

Any adjustements required are stated as commments and must be resolved before requesting another review.

## Definition of Ready 
  User story or task is defined as ready or ready to start when the following is complete.

1. Description of the tasks that must be done
2. Definition of acceptance criteria
3. Team member is assigned

## Definition of Done 
  A task is defined as done when the following is respected.

1. Respects the acceptance criteria
2. Test cases are used to verify the code's execution
3. Code is reviewed by another team member and passes verification
4. Code is ready to merge into main



