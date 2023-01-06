# Robot Factory code challenge

## Description

At Robot Factory Inc. we sell configurable Robots, you can configure one for us to manufacture it.

When ordering a robot, a customer must configure the following parts:
- Face (Humanoid, LCD or Steampunk)
- Material (Bioplastic or Metallic)
- Arms (Hands or Grippers)
- Mobility (Wheels, Legs or Tracks)

An order will be valid if it contains one, and only one, part of face, material, arms and mobility.

If an order is valid and there are enough parts to assemble the robot:
- The priced order should be calculated
- The stock must be adjusted to reflect the fact that parts are being used in robot manufacturing. 

## What to do

Given a stock of:
```bash
Code    Price   Available  Part  						
————————————————————————————————————————————————————
A      10.28     9	       Humanoid Face  
B      24.07     7	       LCD Face
C      13.30     0	       Steampunk Face
D      28.94     1	       Arms with Hands
E      12.39     3	       Arms with Grippers
F      30.77     2	       Mobility with Wheels
G      55.13     15	       Mobility with Legs
H      50.00     7	       Mobility with Tracks
I      90.12	 92	       Material Bioplastic
J      82.31	 15	       Material Metallic
```

Given a list of part codes, implement a robot creation order:

*Input*
`HTTP POST /orders { "components": ["I","A","D","F"] }`

*Output*
`201 {"order_id": "some-id", "total": 160.11 }`

## Requirements
- If you find any functional requirements missing - you are welcome to decide on your own how the system should behave. We'd also be happy to hear back from you why did you make certain decision
- You can use **kotlin** or **java** with either **gradle** (version >= 6.0.1). Unfortunately, we can not send a zip with the wrapper because of security reasons
- We are providing a **kotlin** skeleton with all dependencies and a few acceptance tests to check if the problem is working, but *all the code should be fully tested at all levels*. You are free to modify the acceptance tests as you find necessary, just make sure that the functional requirements are satisfied.
- All tests should pass using `gradle test`
- No additional dependencies are allowed on top of those already specified in `build.gradle.kts`. The project is configured to run succesfull with current version of dependencies. If you wish to update them though, feel free to
- To make things a bit easier, don't use any database or library to manage persistence, this restriction includes JPA, spring-data, ORMs and any physical database.
- Please write clean code
- We are going to look for good design and architecture


We'd like to see a solution that you would be happy and proud to deploy to live environment


Have fun!
