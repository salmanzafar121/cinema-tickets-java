# Cinema Tickets Service

This project is a simple implementation of a cinema ticket purchasing service, created as part of a coding exercise.

---

## My Approach

I focused on keeping the implementation simple, clean, and easy to understand.

The main logic is handled in `TicketServiceImpl`, where:
- All inputs are validated before doing any processing
- Business rules are checked early to reject invalid requests
- Ticket counts are grouped and calculated efficiently
- Total cost and number of seats are calculated separately

I broke the logic into smaller helper methods (for validation, counting, and calculations) to keep the code readable and maintainable.

The overall flow is:
1. Validate input  
2. Apply business rules  
3. Calculate total cost and seats  
4. Call external services for payment and seat reservation  

---

## Assumptions

- Any account ID greater than 0 is valid and has sufficient funds
- External services (`TicketPaymentService` and `SeatReservationService`) always succeed
- Infants do not pay for a ticket and do not get a seat
- Each infant must be accompanied by an adult
- A purchase request is considered invalid if:
  - No tickets are requested
  - Total number of tickets exceeds 25
  - Child or Infant tickets are requested without at least one Adult ticket
  - Number of infants exceeds number of adults

---

## Notes

- No changes were made to third-party code, as required
- `TicketTypeRequest` is treated as an immutable object
- The solution is designed to be easy to extend if new rules or ticket types are introduced in the future
