# The Elevator

## The Elevator
* Provide code that simulates an elevator.
* Document all assumptions and any features that weren't implemented.

## Implementation Narrative
For this implementation of a elevator simulator, I aimed to keep the system as simple and contained as possible.
A true digital twin of an elevator would be better simulated with many interconnecting components (and likely 
deployed to a cloud service), but given the time constraints, staying local to a single set of files avoided many
larger complications, particularly security-related, that come from a scaled deployment.

Each section of this model was built using TDD, looking for edge cases where the previous implementation failed.
I assumed a simple, above ground only building for starters, with call buttons on each floor.  I assumed a few simple
use cases of: going to a floor and calling the car to a floor. The assumption is always that the car completes its
journey before responding to calls, unless it can satisfy the call en route to its destination (i.e. going from floor
1 to 5, a call comes in to go up from floor 2).

Future work is documented below as well.  All future work was only not implemented due to time constraints.  Adding
basements and skipping floors should be relatively simple, and will have the added side benefit of breaking the
tight coupling with the Array implementation.  Adding an elevator bank where multiple cars can respond to a single
request is a bit more involved, and would be the place where further modularizing the code would need to happen.  I
envision encapsulating the elevators and the bank and sending messages between them to handle the state.

I like the central control mechanism of just operating off of the queue.  The interrupts and call handling deviate
from that by forcing the doors open at times rather than adding "open" to the front of the queue.  I also like the
fault tolerance on going up or down, whereby the doors are always checked closed before movement.  I do not like most 
of the logic design inside the functions, as they feel too wordy.  (I have refactored this logic a little bit now, and
it feels better, if not great.)

And while kicking the Elevator class itself into a thread allowed me to better mimic its real world usage,
it made the other functions become less directly testable, which is something I would prefer to refactor back out.

Other things of note: When you review the commit log (which you should - I left some dummies in there), my first commit
was to build up a pipeline.  While this has no "production" endpoint, "The pipeline to production is feature 0" is a
mantra I hold dear and a goal I always strive for.

## Requirements
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
- [x] Elevator needs to validate floor requests

## Future Work
- [ ] Elevator threading needs to be safety validated 
- [ ] Elevator needs a bank with external call buttons
- [ ] Elevator bank represents all floors
- [ ] Elevator bank can hold multiple elevators
- [ ] Elevator should be able to accommodate basements
- [ ] Elevator should (optionally) skip floors if the building doesn't have them (13)
- [ ] Elevator needs an interface
