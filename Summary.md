> Changing the structure :
Moving Prompt function from kotlin to python, it easier for interaction and understand


With Kotlin :
Kotlin Backend : AISummary.kt
Format Prompt -> call Python service

Python Service : 
Receive Prompt -> calls ClaudeAPI -> return Summary


------Python Structure--------
Kotlin BackEnd  : Send trip data (json) : call python service

Python Service : Perform all AI TASK
1) Format Prompt -> call ClaudeApi -> return Summary

