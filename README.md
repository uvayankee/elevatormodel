# The Elevator

The Elevator
* Provide code that simulates an elevator.
* Document all assumptions and any features that weren't implemented.



Requirements
- [x] Starts at floor 1 with doors open
- [x] Elevator needs to know what floor its on
- [x] Elevator needs to provide a set of floors it can visit
- [x] Elevator needs to accept a floor choice
- [x] Elevator needs to go to that floor
- [x] Elevator needs to open doors after it arrives at the destination floor
- [x] Elevator needs to close doors before it starts moving
- [x] Elevator needs to more one floor at a time (actionlog for now)
- [x] Elevator needs to be callable to a floor
- [x] Elevator needs to stop on floors it is called to if they are on the way (request for same direction)
- [x] Elevator needs to prioritize current passengers over calls
- [x] Elevator needs to run on its own

Future Work
- [ ] Elevator threading needs to be safety validated 
- [ ] Elevator needs a bank with external call buttons
- [ ] Elevator bank represents all floors
- [ ] Elevator bank can hold multiple elevators
- [ ] Elevator should be able to accommodate basements
- [ ] Elevator should (optionally) skip floors if the building doesn't have them (13)
- [ ] Elevator needs an interface
