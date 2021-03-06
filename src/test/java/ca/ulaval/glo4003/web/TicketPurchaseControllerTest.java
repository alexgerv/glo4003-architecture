package ca.ulaval.glo4003.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import ca.ulaval.glo4003.domain.match.NoAvailableTicketsException;
import ca.ulaval.glo4003.domain.match.Section;
import ca.ulaval.glo4003.domain.match.Ticket;
import ca.ulaval.glo4003.domain.payment.InvalidCreditCardException;
import ca.ulaval.glo4003.domain.payment.TicketPurchaseFacade;
import ca.ulaval.glo4003.domain.shoppingCart.ShoppingCart;
import ca.ulaval.glo4003.web.converters.SectionViewConverter;
import ca.ulaval.glo4003.web.viewmodels.CreditCardViewModel;
import ca.ulaval.glo4003.web.viewmodels.SectionViewModel;

public class TicketPurchaseControllerTest {

    private static final String SECTION_DETAIL = "sectionDetails";
    private static final String A_VENUE = "Stade Telus";
    private static final String A_DATE = "09/09/2013";
    private static final String A_SECTION_NAME = "A";
    private static final int A_NUMBER_OF_TICKET_TO_BUY = 10;
    private static final String SECTION_IDENTIFIER = "sections";
    private static final String RECEIPT_PAGE = "ticketPurchaseReceipt";
    private static final long CREDIT_CARD_NUMBER = 12345;
    private static final String CREDIT_CARD_TYPE = "VASI";
    private static final Object CART_PAGE = "cart";

    @Mock
    private Model model;
    @Mock
    private SectionViewConverter sectionConverter;
    @Mock
    private SectionViewModel sectionViewModel;
    @Mock
    private CreditCardViewModel creditCardViewModel;
    @Mock
    private TicketPurchaseFacade ticketPurchaseFacade;
    @Mock
    ShoppingCart shoppingCart;
    @Mock
    Section aSection;

    Map<Section, List<Ticket>> cartContent = new HashMap<Section, List<Ticket>>();
    ArrayList<SectionViewModel> expectedSectionViewModel = new ArrayList<SectionViewModel>();

    @InjectMocks
    private TicketPurchaseController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        expectedSectionViewModel.add(sectionViewModel);
        controller = new TicketPurchaseController(ticketPurchaseFacade, sectionConverter, shoppingCart);

    }

    @Test
    public void whenReviewingAPurchaseTheSectionInformationsArePassedToTheView() {
        doReturn(aSection).when(ticketPurchaseFacade).retriveSection(A_VENUE, A_DATE, A_SECTION_NAME);
        doReturn(sectionViewModel).when(sectionConverter).convert(aSection);
        controller.reviewSelectedTicketsForSection(A_VENUE,
                                                   A_DATE,
                                                   A_SECTION_NAME,
                                                   A_NUMBER_OF_TICKET_TO_BUY,
                                                   model,
                                                   creditCardViewModel);
        verify(model, times(1)).addAttribute(SECTION_IDENTIFIER, expectedSectionViewModel);
    }

    @Test
    public void whenPurchasingTicketsThatAreAvailableWeAreRedirectedToTheReceiptPage() {
        doReturn(aSection).when(ticketPurchaseFacade).retriveSection(A_VENUE, A_DATE, A_SECTION_NAME);
        doReturn(sectionViewModel).when(sectionConverter).convert(aSection);
        assertEquals(RECEIPT_PAGE, controller.purchaseSelectedTicketsForSection(A_VENUE,
                                                                                A_DATE,
                                                                                A_SECTION_NAME,
                                                                                A_NUMBER_OF_TICKET_TO_BUY,
                                                                                model,
                                                                                creditCardViewModel));
    }

    @Test
    public void whenPurchasingTicketsThatAreNotAvailableWeAreRedirectedToTheSectionDetailsView() throws InvalidCreditCardException {
        doReturn(aSection).when(ticketPurchaseFacade).retriveSection(A_VENUE, A_DATE, A_SECTION_NAME);
        doReturn(sectionViewModel).when(sectionConverter).convert(aSection);
        doReturn(CREDIT_CARD_NUMBER).when(creditCardViewModel).getNumber();
        doReturn(CREDIT_CARD_TYPE).when(creditCardViewModel).getType();
        doThrow(new NoAvailableTicketsException("")).when(ticketPurchaseFacade)
                                                    .processPurchase(A_VENUE,
                                                                     A_DATE,
                                                                     A_SECTION_NAME,
                                                                     A_NUMBER_OF_TICKET_TO_BUY,
                                                                     CREDIT_CARD_NUMBER,
                                                                     CREDIT_CARD_TYPE);
        assertEquals(SECTION_DETAIL, controller.purchaseSelectedTicketsForSection(A_VENUE,
                                                                                  A_DATE,
                                                                                  A_SECTION_NAME,
                                                                                  A_NUMBER_OF_TICKET_TO_BUY,
                                                                                  model,
                                                                                  creditCardViewModel));
    }

    @Test
    public void whenPurchasingTicketsWithAnInvalidCreditCardWeAreRedirectedToTheSectionDetailsView() throws InvalidCreditCardException {
        doReturn(aSection).when(ticketPurchaseFacade).retriveSection(A_VENUE, A_DATE, A_SECTION_NAME);
        doReturn(sectionViewModel).when(sectionConverter).convert(aSection);
        doReturn(CREDIT_CARD_NUMBER).when(creditCardViewModel).getNumber();
        doReturn(CREDIT_CARD_TYPE).when(creditCardViewModel).getType();
        doThrow(new InvalidCreditCardException("")).when(ticketPurchaseFacade)
                                                   .processPurchase(A_VENUE,
                                                                    A_DATE,
                                                                    A_SECTION_NAME,
                                                                    A_NUMBER_OF_TICKET_TO_BUY,
                                                                    CREDIT_CARD_NUMBER,
                                                                    CREDIT_CARD_TYPE);
        assertEquals(SECTION_DETAIL, controller.purchaseSelectedTicketsForSection(A_VENUE,
                                                                                  A_DATE,
                                                                                  A_SECTION_NAME,
                                                                                  A_NUMBER_OF_TICKET_TO_BUY,
                                                                                  model,
                                                                                  creditCardViewModel));
    }

    @Test
    public void whenPurchasingTicketsFromTheCartWithAnInvalidCreditCardWeAreRedirectedToTheCart() throws InvalidCreditCardException {
        doReturn(aSection).when(ticketPurchaseFacade).retriveSection(A_VENUE, A_DATE, A_SECTION_NAME);
        doReturn(sectionViewModel).when(sectionConverter).convert(aSection);
        doReturn(CREDIT_CARD_NUMBER).when(creditCardViewModel).getNumber();
        doReturn(CREDIT_CARD_TYPE).when(creditCardViewModel).getType();
        doThrow(new InvalidCreditCardException("")).when(ticketPurchaseFacade).processCartPurchase(cartContent,
                                                                                                   CREDIT_CARD_NUMBER,
                                                                                                   CREDIT_CARD_TYPE);
        assertEquals(CART_PAGE, controller.purchaseCartContent(model, creditCardViewModel));
    }

    @Test
    public void whenPurchasingCartThatAreAvailableWeAreRedirectedToTheHomeView() {
        doReturn(aSection).when(ticketPurchaseFacade).retriveSection(A_VENUE, A_DATE, A_SECTION_NAME);
        doReturn(sectionViewModel).when(sectionConverter).convert(aSection);
        assertEquals(RECEIPT_PAGE, controller.purchaseCartContent(model, creditCardViewModel));
    }

}
