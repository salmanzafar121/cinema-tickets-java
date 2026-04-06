package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.EnumMap;
import java.util.Map;

public class TicketServiceImpl implements TicketService {

    private static final int MAX_TICKETS = 25;

    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;
    private static final int INFANT_PRICE = 0;

    private final TicketPaymentService paymentService = new TicketPaymentServiceImpl();
    private final SeatReservationService seatService = new SeatReservationServiceImpl();

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        validateAccount(accountId);
        validateRequests(ticketTypeRequests);

        Map<TicketTypeRequest.Type, Integer> ticketCount = countTickets(ticketTypeRequests);

        int adultCount = ticketCount.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int childCount = ticketCount.getOrDefault(TicketTypeRequest.Type.CHILD, 0);
        int infantCount = ticketCount.getOrDefault(TicketTypeRequest.Type.INFANT, 0);

        validateBusinessRules(adultCount, childCount, infantCount);

        int totalTickets = adultCount + childCount + infantCount;
        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than 25 tickets");
        }

        int totalAmount = calculateAmount(adultCount, childCount);
        int totalSeats = calculateSeats(adultCount, childCount);

        paymentService.makePayment(accountId, totalAmount);
        seatService.reserveSeat(accountId, totalSeats);
    }

    private void validateAccount(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account id");
        }
    }

    private void validateRequests(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException("No ticket requests provided");
        }
    }

    private Map<TicketTypeRequest.Type, Integer> countTickets(TicketTypeRequest... requests) {
        Map<TicketTypeRequest.Type, Integer> counts = new EnumMap<>(TicketTypeRequest.Type.class);

        for (TicketTypeRequest request : requests) {
            if (request == null || request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Invalid ticket request");
            }

            counts.merge(request.getTicketType(), request.getNoOfTickets(), Integer::sum);
        }

        return counts;
    }

    private void validateBusinessRules(int adult, int child, int infant) {
        if (adult == 0 && (child > 0 || infant > 0)) {
            throw new InvalidPurchaseException("Child/Infant tickets require at least one Adult");
        }

        if (infant > adult) {
            throw new InvalidPurchaseException("Each infant must have an adult");
        }
    }

    private int calculateAmount(int adult, int child) {
        return (adult * ADULT_PRICE) + (child * CHILD_PRICE);
    }

    private int calculateSeats(int adult, int child) {
        return adult + child; // infants do not get seats
    }
}
