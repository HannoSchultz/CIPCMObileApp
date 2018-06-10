package userclasses;

import com.codename1.capture.Capture;
import com.codename1.charts.ChartComponent;
import com.codename1.charts.models.CategorySeries;
import com.codename1.charts.models.SeriesSelection;
import com.codename1.charts.renderers.DefaultRenderer;
import com.codename1.charts.renderers.SimpleSeriesRenderer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.charts.views.PieChart;
import com.codename1.components.FloatingActionButton;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.MultiButton;
import com.codename1.components.OnOffSwitch;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.io.services.TwitterRESTService;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.messaging.Message;
import com.codename1.processing.Result;
import com.codename1.push.Push;
import com.codename1.push.PushCallback;
import generated.StateMachineBase;
import com.codename1.ui.*;
import com.codename1.ui.events.*;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.geom.Shape;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.list.ContainerList;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.FilterProxyListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.DateSpinner;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.util.Resources;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import com.codename1.util.regex.RE;

import com.sun.prism.paint.Color;
//import com.pmovil.nativega.GANative;
//import com.pmovil.nativega.HitBuilders;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import za.co.cipc.webservices.UserWebServices;
import ui.FormProgress;
import za.co.cipc.pojos.User;
//TESTING UPLOAD
/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase {

    boolean isRegStep1Passed = false;
    boolean isRegStep2Passed = false;
    boolean isRegStep3Passed = false;

    boolean isARStep1Passed = false;
    boolean isARStep2Passed = false;
    boolean isARStep3Passed = false;

    boolean isCartStep1Passed = false;

    String ENT_NUMBER;
    ArrayList annualReturnsEntDetails;

    ArrayList<EnterpriseDetails> listEnterpriseDetails;
    ArrayList<TextArea> listTextEnterpriseDetails;
    ArrayList<EnterpriseDetails> listCalculateARTran;

    //private com.pmovil.nativega.Tracker tracker;
    private String[] arrDevices;
    private String action = "";
    private Form current;
    private User oldUser;
    private String devicesString = "";

    private Container contCommunicationc;
    private Button btnProfilePic;
    private Button btnProfileName;
    private EncodedImage placeHolder;
    private Form newTask;

    private static String taskId = "";
    private static String defaultEmail = "";
    private static String defaultPassword = "";
    private static FormProgress formProgress;
    private String message = "";

    private static String AGENT_CODE = "";

    EnterpriseDetails enterpriseDetails;

    public StateMachine(String resFile) {
        super(resFile);
        // do not modify, write code in initVars and initialize class members there,
        // the constructor might be invoked too late due to race conditions that might occur
    }

    protected void initVars(Resources res) {

        NetworkManager.getInstance().addErrorListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        if (Display.getInstance().isTablet()) {
            Toolbar.setPermanentSideMenu(true);
        }

        if (Display.getInstance().isSimulator()) {
            AGENT_CODE = "KD7788";
            //UserWebServices u = new UserWebServices();
            //String responseCall = u.Namereservation_MOBI(AGENT_CODE, "name1", "name2", "name3", "name4");
            //Log.p("responseCall=" + responseCall, Log.DEBUG);
        }

    }

    @Override
    protected String getFirstFormName() {

        if (Display.getInstance().isSimulator()) {//Prepoluate with Debug info

            defaultEmail = "KD7788";
            defaultPassword = "fijiaudi";

            Log.setLevel(Log.DEBUG);
            Log.p("issimulator", Log.DEBUG);

            return "Login";

        } else {
            Log.setLevel(Log.REPORTING_PRODUCTION);//To disable debug information
            return "Splash";

        }

    }

    public Toolbar analytics(Form f, String screenName) {

        Toolbar toolbar = f.getToolbar();
        if (toolbar == null) {
            toolbar = new Toolbar();
            f.setToolbar(toolbar);
        }

        if (toolbar.getTitleComponent() instanceof Label) {

            Label title = (Label) toolbar.getTitleComponent();
            title.setUIID("LabelWhiteCenter");
            toolbar.setTitle(screenName);
            toolbar.setUIID("LabelWhiteCenter");
            title.setIcon(null);

        }

        return toolbar;
    }

    public void closeMenu(Form f, boolean isClose) {

        Toolbar tb = f.getToolbar();

        if (isClose) {
            tb.closeSideMenu();

        } else {
            tb.openSideMenu();

        }
    }

    public void showProfile(final Form f) {
        formProgress = new FormProgress(f);
        closeMenu(f, true);

    }

    public void uploadProfileImage(String title, Form f, String path, EncodedImage placeHolder, Label lblImage) {

        formProgress = new FormProgress(f);
        formProgress.removeProgress();

    }

    public void showNameReservation(Form f, String taskType) {
        formProgress = new FormProgress(f);
        closeMenu(f, true);

        Container contentPane = f.getContentPane();
        contentPane.removeAll();
        Container contTasks = (Container) createContainer("/theme", "ContTasks");

        Label lblLine1 = (Label) findByName("lblLine1", contTasks);

        TextField txtName1 = (TextField) findByName("txtName1", contTasks);
        TextField txtName2 = (TextField) findByName("txtName2", contTasks);
        TextField txtName3 = (TextField) findByName("txtName3", contTasks);
        TextField txtName4 = (TextField) findByName("txtName4", contTasks);

        Label lblName1Response = (Label) findByName("lblName1Response", contTasks);
        Label lblName2Response = (Label) findByName("lblName2Response", contTasks);
        Label lblName3Response = (Label) findByName("lblName3Response", contTasks);
        Label lblName4Response = (Label) findByName("lblName4Response", contTasks);

        Button btnVerify = (Button) findByName("btnVerify", contTasks);
        Button btnLodge = (Button) findByName("btnLodge", contTasks);

        btnVerify.addActionListener((ActionListener) (ActionEvent evt) -> {
            String name1 = txtName1.getText();
            String name2 = txtName2.getText();
            String name3 = txtName3.getText();
            String name4 = txtName4.getText();

            UserWebServices u = new UserWebServices();
            ArrayList<NameSearchObject> arrayList = u.search_name_MOBI(AGENT_CODE, name1, name2, name3, name4);

            for (int i = 0; i < arrayList.size(); i++) {
                int count = i + 1;
                Label lblResponse = (Label) findByName("lblName" + count + "Response", contTasks);
                NameSearchObject n = arrayList.get(i);
                if (n.isIsValid()) {
                    lblResponse.setText("Might be available");
                    lblResponse.setUIID("LabelGreen");
                } else {
                    lblResponse.setText("Is not available");
                    lblResponse.setUIID("LabelRed");

                }
            }

            //lblLine1.scrollRectToVisible(BACK_COMMAND_ID, BACK_COMMAND_ID, BACK_COMMAND_ID, BACK_COMMAND_ID, contentPane);
            contTasks.repaint();

        });

        btnLodge.addActionListener((ActionListener) (ActionEvent evt) -> {
            String name1 = txtName1.getText();
            String name2 = txtName2.getText();
            String name3 = txtName3.getText();
            String name4 = txtName4.getText();
            UserWebServices u = new UserWebServices();
            String responseCall = u.Namereservation_MOBI(AGENT_CODE, name1, name2, name3, name4);

            if (responseCall != null && responseCall.length() > 0) {
                Dialog.show("Success", responseCall, "Ok", null);
                showCart(f);
            } else {
                Dialog.show("Error", "Error occurred while processing your request", "Ok", null);
            }
        });

        f.add(contTasks);

        if (formProgress != null) {
            formProgress.removeProgress();
        }

    }

    public void showDashboard(final Form f) {
        formProgress = new FormProgress(f);
        closeMenu(f, true);

        analytics(f, "Dashboard");

        Container contentPane = f.getContentPane();
        contentPane.removeAll();
        Container cont = (Container) createContainer("/theme", "ContDashBoard");

        MultiButton mbTasks = (MultiButton) findByName("mbTasks", cont);

        mbTasks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                formProgress = new FormProgress(f);
                showNameReservation(f, Const.TASK_TODAY);
            }
        });

        MultiButton mbCurrency = (MultiButton) findByName("mbCurrency", cont);
        mbCurrency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                formProgress = new FormProgress(f);
                showAnnualReturns(f);
            }
        });

        MultiButton mbCart = (MultiButton) findByName("mbCart", cont);
        mbCart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                formProgress = new FormProgress(f);
                showCart(f);
            }
        });

        mbTasks.setTextLine1("Name Reservations");
        mbTasks.setTextLine2("Lodge Name(s)");

        mbCurrency.setTextLine1("Annual Returns");
        mbCurrency.setTextLine2("Submit Enterprise Returns");

        mbCart.setTextLine1("Shopping Cart");
        mbCart.setTextLine2("Pay Now for CIPC Services");

        f.add(cont);
        if (formProgress != null) {
            formProgress.removeProgress();
        }
        closeMenu(f, true);

    }

    public static SwipeableContainer createRankWidget(String line1, String line2, ActionListener action) {
        MultiButton button = new MultiButton(line1);
        button.addActionListener(action);
        button.setUIID("MultiButtonColor");
        button.setName("MultiButtonColor");
        button.setTextLine2(line2);
        SwipeableContainer s = new SwipeableContainer(null, createActionsProject(), button);

        return s;
    }

    private static Container createActionsProject() {
        Container flow = new Container(new FlowLayout());
        Button delete = new Button("Delete");
        delete.addActionListener(e -> ToastBar.showMessage("Not implemented yet", FontImage.MATERIAL_MESSAGE, 5000));
        flow.add(delete);
        return flow;
    }

    public void showAnnualReturns(final Form f) {
        formProgress = new FormProgress(f);
        closeMenu(f, true);
        analytics(f, "Annual Returns");
        current = f;
        Container contentPane = f.getContentPane();
        contentPane.removeAll();
        Container contProjects = (Container) createContainer("/theme", "ContProjects");
        Container contStep2 = (Container) findByName("contStep2", contProjects);

        Tabs tabs = (Tabs) findByName("Tabs", contProjects);
        tabs.setSwipeActivated(false);

        //Step 1
        TextField txtStep1a = (TextField) findByName("txtStep1a", tabs);
        TextField txtStep1b = (TextField) findByName("txtStep1b", tabs);
        TextField txtStep1c = (TextField) findByName("txtStep1c", tabs);

        if (Display.getInstance().isSimulator()) {//2011100088 & K2013064531 & 2014 004548 07
            txtStep1a.setText("2011");
            txtStep1b.setText("100088");
            txtStep1c.setText("07");
        }

        txtStep1a.getParent().repaint();

        Button btnStep1RetrieveDetails = (Button) findByName("btnStep1RetrieveDetails", tabs);

        //Step 2
        Label lblStep2EnterpriseNumber = (Label) findByName("lblStep2EnterpriseNumber", contStep2);
        Label lblStep2EnterpriseName = (Label) findByName("lblStep2EnterpriseName", contStep2);
        Label lblStep2EnterpriseType = (Label) findByName("lblStep2EnterpriseType", contStep2);
        Label lblStep2EnterpriseStatus = (Label) findByName("lblStep2EnterpriseStatus", contStep2);
        Label lblStep2RegistrationDate = (Label) findByName("lblStep2RegistrationDate", contStep2);

        btnStep1RetrieveDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                ENT_NUMBER = txtStep1a.getText() + "/" + txtStep1b.getText()
                        + "/" + txtStep1c.getText();
                //ENT_NUMBER  = "K2013064531";

                UserWebServices u = new UserWebServices();
                enterpriseDetails = u.soap_GetEnterpriseDetails(ENT_NUMBER); //"K2013064531");//2014 / 016320 /  07

                if (enterpriseDetails != null) {
                    lblStep2EnterpriseNumber.setText(enterpriseDetails.getEnt_no());
                    lblStep2EnterpriseName.setText(enterpriseDetails.getEnt_name());
                    lblStep2EnterpriseType.setText(enterpriseDetails.getEnt_type_descr());
                    lblStep2EnterpriseStatus.setText(enterpriseDetails.getEnt_status_descr());
                    lblStep2RegistrationDate.setText(enterpriseDetails.getReg_date());

                    tabs.setSelectedIndex(1);

                } else {
                    Dialog.show("Error", "Could not obtain enterprise details.", "Ok", null);
                }

            }
        });

        Button btnStep2Confirm = (Button) findByName("btnStep2Confirm", tabs);

        //Step 3//Please enter Annual Turnover for the current filing year, 2018:
        Container contStep3Turnovers = (Container) findByName("contStep3Turnovers", tabs);
        contStep3Turnovers.removeAll();

        btnStep2Confirm.addActionListener((ActionListener) (ActionEvent evt) -> {

            UserWebServices u = new UserWebServices();
            listEnterpriseDetails = u.GetAREntTranDetails(ENT_NUMBER, AGENT_CODE);

            if (listEnterpriseDetails.size() > 0) {

                listTextEnterpriseDetails = new ArrayList<>();//store turnover textfields
                for (EnterpriseDetails ent : listEnterpriseDetails) {
                    Container cont0 = new Container(BoxLayout.y());
                    cont0.setUIID("CalendarDay");
                    TextArea txt0a = new TextArea();
                    txt0a.setText("Please enter Annual Turnover for " + ent.getAr_year());
                    txt0a.setEnabled(false);
                    txt0a.setEditable(false);
                    TextArea txt0b = new TextArea();
                    txt0b.setHint("Amount in rands");
                    txt0b.setConstraint(TextArea.DECIMAL);
                    listTextEnterpriseDetails.add(txt0b);
                    cont0.add(txt0a).add(txt0b);
                    contStep3Turnovers.add(cont0);
                }
                contStep3Turnovers.repaint();

            }

            tabs.setSelectedIndex(2);
        });

        Button btnStep3CalcOutAmount = (Button) findByName("btnStep3CalcOutAmount", tabs);

        Container contStep4AnnualReturns = (Container) findByName("contStep4AnnualReturns", tabs);

        Label lblTotalDue = (Label) findByName("lblTotalDue", tabs);

        btnStep3CalcOutAmount.addActionListener((ActionListener) (ActionEvent evt) -> {

            String table1LineStart = "<Table1 diffgr:id=\"Table11\" msdata:rowOrder=\"0\" diffgr:hasChanges=\"inserted\">\n";

            String table11Body = "";

            for (int i = 0; i < listEnterpriseDetails.size(); i++) {

                EnterpriseDetails entDetails = listEnterpriseDetails.get(i);
                TextArea txtTurnover = listTextEnterpriseDetails.get(i);

                table11Body += "<ent_no>" + entDetails.getEnt_no() + "</ent_no>\n"
                        + "                     <ar_year>" + entDetails.getAr_year() + "</ar_year>\n"
                        + "                     <ar_month>" + entDetails.getAr_month() + "</ar_month>\n"
                        + "                     <turnover>" + txtTurnover.getText() + "</turnover>\n"
                        + "                     <ent_type_code>" + entDetails.getEnt_type_code() + "</ent_type_code>\n";

            }

            String table1LineEnd = "</Table1>\n";

            String dataSet = table1LineStart + table11Body + table1LineEnd;

            Log.p("dataset=" + dataSet, Log.DEBUG);

            UserWebServices u = new UserWebServices();
            listCalculateARTran = u.CalculateARTranData(dataSet);
            contStep4AnnualReturns.removeAll();

            if (listCalculateARTran.isEmpty()) {
                Log.p("listCalculateARTran=0", Log.DEBUG);
            } else {
                Log.p("listCalculateARTran=" + listCalculateARTran.size(), Log.DEBUG);
            }

            for (int i = 0; i < listCalculateARTran.size(); i++) {

                EnterpriseDetails e = listCalculateARTran.get(i);
                MultiButton mb = new MultiButton();
                mb.setUIID("CalendarDay");
                mb.setUIIDLine1("MultiButtonBlack");
                mb.setUIIDLine2("MultiButtonBlack");
                mb.setUIIDLine3("MultiButtonBlack");
                mb.setUIIDLine4("MultiButtonBlack");
                mb.setTextLine1("Enterprise No: " + e.getEnt_no());
                mb.setTextLine2("Reference No: " + e.getReference_no());
                mb.setTextLine3("AR Year: " + e.getAr_year() + ", Turnover: R" + e.getTurnover());
                mb.setTextLine4("AR Amount: R" + e.getAr_amount() + ", Penalty: R" + e.getAr_penalty());

                contStep4AnnualReturns.add(mb);

            }

            if (listCalculateARTran.size() > 0) {
                EnterpriseDetails lastObject = listCalculateARTran.get(listCalculateARTran.size() - 1);
                lblTotalDue.setText("Total Due: R" + lastObject.getAr_total());
                lblTotalDue.repaint();
            }

            contStep4AnnualReturns.repaint();

            tabs.setSelectedIndex(3);

        });

        //Step 4
        Button btnStep4AddToCart = (Button) findByName("btnStep4AddToCart", tabs);
        btnStep4AddToCart.addActionListener((ActionListener) (ActionEvent evt) -> {

            Dialog.show("Success", "Annual Return (s) added to shopping cart", "Ok", null);
            showCart(f);

        });

        f.add(contProjects);
        if (formProgress != null) {
            formProgress.removeProgress();
        }

    }

    public void showCart(final Form f) {
        formProgress = new FormProgress(f);
        closeMenu(f, true);
        analytics(f, "Shopping Cart");
        current = f;
        Container contentPane = f.getContentPane();
        contentPane.removeAll();
        Container cont = (Container) createContainer("/theme", "ContCart");

        Tabs tabs = (Tabs) findByName("Tabs", cont);
        tabs.setSwipeActivated(false);

        UserWebServices u = new UserWebServices();
        User user = new User();
        user.setAgent_code(AGENT_CODE);
        Map map = u.getCart(user);

        Container contStep1AnnualReturns = (Container) findByName("contStep1AnnualReturns", tabs);
        contStep1AnnualReturns.removeAll();
        Container contStep1EServices = (Container) findByName("contStep1EServices", tabs);
        contStep1EServices.removeAll();
        Label lblTotal = (Label) findByName("lblTotal", tabs);

        String CustomerCode = map.get("CustomerCode").toString();
        double AnnualReturnsTotalAmount = Double.parseDouble(map.get("AnnualReturnsTotalAmount").toString());
        double ItemDataTotalAmount = Double.parseDouble(map.get("ItemDataTotalAmount").toString());
        double TotalAmount = Double.parseDouble(map.get("TotalAmount").toString());
        double ItemsCount = Double.parseDouble(map.get("ItemsCount").toString());
        ArrayList AnnualReturns = (ArrayList) map.get("AnnualReturns");
        ArrayList CartItems = (ArrayList) map.get("CartItems");

        double eserviceTotal = 0.0;
        for (Object o : CartItems) {

            Container contItem = new Container(BoxLayout.y());
            contItem.setUIID("CalendarDay");
            Map m = (Map) o;
            String ItemType = m.get("ItemType").toString();
            String StatusDate = m.get("StatusDate").toString();
            double ReferenceNumber = Double.parseDouble(m.get("ReferenceNumber").toString());
            String EnterpriseNumber = m.get("EnterpriseNumber").toString();
            String FormCode = m.get("FormCode").toString();
            double TotalAmountItemType = Double.parseDouble(m.get("TotalAmount").toString());
            eserviceTotal += TotalAmountItemType;

            MultiButton mb = new MultiButton();
            mb.setUIID("Label");
            mb.setUIIDLine1("MultiButtonBlack");
            mb.setUIIDLine2("MultiButtonBlack");
            mb.setUIIDLine3("MultiButtonBlack");
            mb.setUIIDLine4("MultiButtonBlack");

            mb.setTextLine1("Reference No: " + ReferenceNumber);
            mb.setTextLine2("Enterprise No: " + EnterpriseNumber);
            mb.setTextLine3("Service: " + ItemType);
            mb.setTextLine3("Item Cost: R" + TotalAmountItemType);

            Container c0 = new Container();
            Button btnRemove0 = new Button("REMOVE");
            c0.add(btnRemove0);
            contItem.add(mb).add(c0);
            contStep1EServices.add(contItem);

        }
        ArrayList Items = (ArrayList) map.get("Items");

        lblTotal.setText("Total: R" + eserviceTotal);
        lblTotal.repaint();

        Button btnCheckout = (Button) findByName("btnCheckout", tabs);
        btnCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                tabs.setSelectedIndex(1);
            }
        });

        Button btnPayNow = (Button) findByName("btnPayNow", tabs);
        btnPayNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                formProgress = new FormProgress(f);
                try {

                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    Log.e(e);
                }
                formProgress.removeProgress();

                Dialog.show("Processed", "Payment processed", "Ok", null);
            }
        });

        f.add(cont);
        if (formProgress != null) {
            formProgress.removeProgress();
        }

    }

    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
        Font smallFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.SIZE_SMALL, Font.STYLE_BOLD);
        Font medFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.SIZE_MEDIUM, Font.STYLE_BOLD);
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(smallFont.getHeight() * 0.5f);
        renderer.setLegendTextSize(smallFont.getHeight() * 0.5f);
        renderer.setMargins(new int[]{medFont.getHeight(), medFont.getHeight(), medFont.getHeight(), medFont.getHeight()});
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    public void addBack(Form f, Form prev) {
        Toolbar toolbar = f.getToolbar();
        Command back = new Command("Back") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
                prev.showBack();
            }

        };
        toolbar.setBackCommand(back);
    }

    public void changeFloatingButtonStyle(FloatingActionButton floatingButton) {

        floatingButton.getSelectedStyle().setBgColor(Const.FLOATING_BUTTON_PRESSED);
        floatingButton.getUnselectedStyle().setBgColor(Const.FLOATING_BUTTON);
        floatingButton.getPressedStyle().setBgColor(Const.FLOATING_BUTTON_PRESSED);

        floatingButton.getUnselectedStyle().setFgColor(Const.FLOATING_BUTTON_TEXT);
        floatingButton.getPressedStyle().setFgColor(Const.FLOATING_BUTTON_TEXT);
        floatingButton.getSelectedStyle().setFgColor(Const.FLOATING_BUTTON_TEXT);
        floatingButton.getStyle().setFgColor(Const.FLOATING_BUTTON_TEXT);

    }

    @Override
    protected void beforeMain(Form f) {

        Toolbar toolbar = analytics(f, "Home");

        current = f;

        showDashboard(f);

        Container contSideMenu = (Container) createContainer("/theme", "ContSideMenu");
        contSideMenu.setScrollableX(false);//This fixed the side menu scroll issue
        contSideMenu.setScrollableY(false);

        btnProfilePic = (Button) findByName("btnProfilePic", contSideMenu);
        btnProfileName = (Button) findByName("btnProfileName", contSideMenu);

        btnProfileName.setText("Update Profile");

        btnProfilePic.addActionListener((ActionListener) (ActionEvent evt) -> {
            showProfile(f);
        });

        btnProfileName.addActionListener((ActionListener) (ActionEvent evt) -> {
            showProfile(f);
        });

        Button btnDashboard = (Button) findByName("btnDashboard", contSideMenu);
        btnDashboard.addActionListener((ActionListener) (ActionEvent evt) -> {
            closeMenu(f, true);
            showDashboard(f);
            closeMenu(f, true);
        });

        Button btnLogout = (Button) findByName("btnLogout", contSideMenu);
        btnLogout.addActionListener((ActionListener) (ActionEvent evt) -> {
            showForm("Login", null);
        });

        Command command = new Command("Update Photo");
        command.putClientProperty("SideComponent", contSideMenu);

        f.getToolbar().addComponentToSideMenu(contSideMenu);

    }

    public String trimEmail(String email) {
        if (email != null) {
            email = email.toLowerCase().trim();
        }
        return email;
    }

    @Override
    protected boolean onLoginLogin() {

        Form f = Display.getInstance().getCurrent();

        Container c = (Container) findByName("containerParent", f);

        String txtCustomerCode = ((TextField) findByName("txtCustomerCode", c)).getText();
        final String password = ((TextField) findByName("txtPassword", c)).getText();

        za.co.cipc.pojos.User user = new za.co.cipc.pojos.User();
        user.setAgent_code(txtCustomerCode);
        user.setParamPassword(password);

        formProgress = new FormProgress(f);

        UserWebServices userWebServices = new UserWebServices();

        za.co.cipc.pojos.User responseUser = userWebServices.get_cust_MOBI(user);

        String errorMessage = "";

        if (responseUser == null) {
            formProgress.removeProgress();
            Log.p("exception" + "", Log.DEBUG);

            errorMessage += "Incorrect Customer Code or password. ";

        }

        String responsePassword = responseUser.getPassword();

        Log.p(password + ":" + responsePassword);

        if (password.equals(responsePassword)) {
            AGENT_CODE = txtCustomerCode;
            return false;
        } else {
            errorMessage += "Invalid Customer Code or Password. ";
        }

        formProgress.removeProgress();

        if (errorMessage != null && errorMessage.length() > 0) {
            showDialog(errorMessage);
            return true;//block
        } else {
            return false;
        }

    }

    public void showDialog(String message) {
        ToastBar.Status status = ToastBar.getInstance().createStatus();
        status.setMessage(message);
        status.setExpires(5000);
        status.show();

    }

    private void automoveToNext(final TextField current, final TextField next) {
        current.addDataChangeListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                if (current.getText().length() == 5) {
                    Display.getInstance().stopEditing(current);
                    String val = current.getText();
                    current.setText(val.substring(0, 4));
                    next.setText(val.substring(4));
                    Display.getInstance().editString(next, 5, current.getConstraint(), next.getText());
                }
            }
        });
    }

    public void isTableInputForm(Form f) {
    }

    @Override
    protected void beforeLogin(final Form f) {

        current = f;

        if (Display.getInstance().isSimulator()) {
            TextField txtCustomerCode = (TextField) findByName("txtCustomerCode", f);
            txtCustomerCode.setText("KD7788");
            TextField txtPassword = (TextField) findByName("txtPassword", f);
            txtPassword.setText("fijiaudi");

        }

    }

    @Override
    protected void beforeRegistration(Form f) {

        Toolbar bar = analytics(f, "Registration");

        isTableInputForm(f);

        Tabs tabs = (Tabs) findByName("Tabs", f);
        tabs.setSwipeActivated(false);
        tabs.hideTabs();

        Button btnStep1Continue = (Button) findByName("btnStep1Continue", tabs);
        Button btnStep2Continue = (Button) findByName("btnStep2Continue", tabs);
        Button btnStep3Next = (Button) findByName("btnStep3Next", tabs);
        Button btnStep4Register = (Button) findByName("btnStep4Register", tabs);
        
        //initialy false
        btnStep2Continue.setEnabled(false);
        btnStep3Next.setEnabled(false);
        btnStep4Register.setEnabled(false);

        btnStep1Continue.addActionListener((ActionListener) (ActionEvent evt) -> {
            isARStep1Passed = true;
            btnStep2Continue.setEnabled(true);
            tabs.setSelectedIndex(1);
        });
        
        btnStep2Continue.addActionListener((ActionListener) (ActionEvent evt) -> {
            isARStep2Passed = true;
            btnStep3Next.setEnabled(true);
            tabs.setSelectedIndex(2);
        });
        
        btnStep3Next.addActionListener((ActionListener) (ActionEvent evt) -> {
            isARStep3Passed = true;
            btnStep4Register.setEnabled(true);
            tabs.setSelectedIndex(3);
        });
        
        btnStep4Register.addActionListener((ActionListener) (ActionEvent evt) -> {
        
        });

        Button btn1 = new Button("1");
        Button btn2 = new Button("2");
        Button btn3 = new Button("3");
        Button btn4 = new Button("4");

        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                tabs.setSelectedIndex(0);
            }
        });

        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (isRegStep1Passed == true) {
                    tabs.setSelectedIndex(1);
                }
            }
        });

        btn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (isRegStep1Passed == true && isRegStep2Passed == true) {
                    tabs.setSelectedIndex(2);
                }
            }
        });

        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (isRegStep1Passed == true && isRegStep2Passed == true
                        && isRegStep3Passed == true) {
                    tabs.setSelectedIndex(3);
                }
            }
        });

        Container contTop = new Container();
        contTop.setUIID("LabelWhite");
        contTop.setLayout(new GridLayout(1, 4));
        contTop.add(btn1).add(btn2).add(btn3).add(btn4);

        f.add(BorderLayout.NORTH, contTop);

        tabs.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(int oldSelected, int newSelected) {

            }
        });

        //tabs.
        Picker pickerCountry = (Picker) findByName("pickerStep2Country", f);
        pickerCountry.setType(Display.PICKER_TYPE_STRINGS);
        pickerCountry.setStrings("Select Country", "Brazil", "South Africa", "Zimbabwe");

        Picker pickerProvince = (Picker) findByName("pickerStep3Province", f);
        pickerProvince.setType(Display.PICKER_TYPE_STRINGS);
        pickerProvince.setStrings("Select Province", "Gauteng", "Kwazulu Natal");

        Container contRadioButtons = (Container) findByName("contRadioButtons", f);

        RadioButton rdYes = (RadioButton) findByName("rdYes", contRadioButtons);
        RadioButton rdNo = (RadioButton) findByName("rdNo", contRadioButtons);

        ButtonGroup btButtonGroup = new ButtonGroup();

        btButtonGroup.add(rdYes);
        btButtonGroup.add(rdNo);

        Command back = new Command("Login") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                current.showBack();
            }

        };
        bar.addCommandToLeftBar(back);
        bar.setBackCommand(back);

    }

    @Override
    protected void beforeForgotPassword(Form f) {
        Toolbar bar = analytics(f, "Forgot Login Details");

        isTableInputForm(f);

        Command back = new Command("Login") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                current.showBack();
            }

        };
        bar.addCommandToLeftBar(back);
        bar.setBackCommand(back);
    }

    @Override
    protected void beforeSplash(Form f) {

    }

    @Override
    protected void postLogin(Form f) {

    }
}
