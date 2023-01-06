# Robot Factory

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

## Architecture & Design decisions

It has been decided to use a hexagonal architecture (or port & adapters), this decision has been made
taking into account the next points:
  - The business logic should be the core of the application
  - The API level should be delegated to a second plan to show that it's only a way to access the business
  - The frameworks from a specific adapter should not condition too much the design of our application, in this specific case Spring 
    boot should be contained on it's port
  - Hexagonal works really cool with DDD

As I feel more comfortable with the functional programming approach about handling errors
I have decided to implement a simple Either (Check Result from F or Either from Scala), In 
a real code I will have used the Either from Arrow.kt and not my basic implementation

// TODO talk about price precision
// TODO atomic transaction

## Versions Updates

As there is an empty skeleton yet (except for some tests) it has been decided to dedicate some time to update the versions for the used language
and the different libraries or frameworks being used.

The main reasons for it are:
 - To try to reduce the risk for some vulnerabilities on old code
 - To get some increase on performance 
 - To get the new features

- Major updates done
    - Kotlin --> 1.8.0
    - Java --> 17
    - Spring Boot --> 2.7.7 

## How to test

- All tests should pass using `gradle test`
