package com.wetrain.client.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by test on 1/21/16.
 */
public class UpdateCreditCardFragmant extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener, FunctionCallback<Object>{

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = ' ';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static int CARD_CVC_TOTAL_SYMBOLS = 3;

    private static final String CARD_TYPE_VISA = "visa";
    private static final String CARD_TYPE_MASTERCARD = "mastercard";
    private static final String CARD_TYPE_AMEX = "american express";
    private static final String CARD_TYPE_DISCOVER = "discover";


    private Card validatedCreditCard;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.update_credit_card_view, container, false);

        final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
        if(clientParseObj != null){
            clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, com.parse.ParseException e) {
                    if(clientParseObj.containsKey("stripeFour")) {
                        if (clientParseObj.getString("stripeFour") != null) {
                            if (clientParseObj.getString("stripeFour").length() > 0) {
                                ((TextView) view.findViewById(R.id.current_credit_card_last_4number)).
                                        setText("Your current credit card is *" + clientParseObj.getString("stripeFour"));

                                ((MainActivity) getActivity()).setOptionsButtonLabel("Update");
                            }
                        }
                    }

                }
            });
        }

        ((EditText) view.findViewById(R.id.card_number_edit_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
                }

                Card card = new Card(s.toString(), getCardMonth(), getCardMonth(), ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).getText().toString());

                CARD_CVC_TOTAL_SYMBOLS = 3;

                String type = card.getType() == null ? "" : card.getType();
                if (type.equalsIgnoreCase(CARD_TYPE_VISA)) {
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.visa), null, null, null);

                } else if (type.equalsIgnoreCase(CARD_TYPE_MASTERCARD)) {
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.master_card), null, null, null);

                } else if (type.equalsIgnoreCase(CARD_TYPE_AMEX)) {
                    CARD_CVC_TOTAL_SYMBOLS = 4;
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.amex), null, null, null);

                } else if (type.equalsIgnoreCase(CARD_TYPE_DISCOVER)) {
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.discover), null, null, null);

                } else {
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_card_number), null, null, null);

                }


                if (card.validateNumber()) {
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).clearFocus();
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setTextColor(Color.BLACK);
                    /*((EditText) view.findViewById(R.id.card_date_edit_text)).setVisibility(View.VISIBLE);
                    ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).setVisibility(View.VISIBLE);*/
                    ((EditText) view.findViewById(R.id.card_date_edit_text)).requestFocus();
                    ((EditText) view.findViewById(R.id.card_date_edit_text)).setSelection(((EditText) view.findViewById(R.id.card_date_edit_text)).getText().length());

                } else {
                    ((EditText) view.findViewById(R.id.card_number_edit_txt)).setTextColor(Color.RED);
                }

                if (card.validateCard()) {
                    ((MainActivity) getActivity()).setOptionButtonEnable(true);

                }else{
                    ((MainActivity) getActivity()).setOptionButtonEnable(false);

                }
            }
        });

        /*((EditText) view.findViewById(R.id.card_date_edit_text)).setVisibility(View.INVISIBLE);
        ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).setVisibility(View.INVISIBLE);*/

        ((EditText) view.findViewById(R.id.card_date_edit_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 1) {
                    int val = Integer.parseInt(s.toString());
                    if (val >= 2) {
                        s.insert(0, "0");
                    }
                }else if(s.length() == 2){
                    int val = Integer.parseInt(s.toString());
                    if(val >= 13){
                        s = new SpannableStringBuilder("1");

                        ((EditText) view.findViewById(R.id.card_date_edit_text)).removeTextChangedListener(this);
                        ((EditText) view.findViewById(R.id.card_date_edit_text)).setText("1");
                        ((EditText) view.findViewById(R.id.card_date_edit_text)).addTextChangedListener(this);
                        ((EditText) view.findViewById(R.id.card_date_edit_text)).setSelection(1);

                    }

                }else if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));

                }


                int month = getCardMonth();
                int year = getCardYear();
                Card card = new Card(((EditText) view.findViewById(R.id.card_number_edit_txt)).getText().toString(), month, year, ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).getText().toString());

                if (card.validateExpiryDate()) {
                    ((EditText) view.findViewById(R.id.card_date_edit_text)).clearFocus();
                    ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).requestFocus();
                    ((EditText) view.findViewById(R.id.card_date_edit_text)).setTextColor(Color.BLACK);
                    ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).setSelection(((EditText) view.findViewById(R.id.card_cvc_edit_txt)).getText().length());

                } else {
                    ((EditText) view.findViewById(R.id.card_date_edit_text)).setTextColor(Color.RED);

                }

                if(card.validateCard()){
                    ((MainActivity) getActivity()).setOptionButtonEnable(true);
                }else{
                    ((MainActivity) getActivity()).setOptionButtonEnable(false);

                }



            }
        });

        ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
                    s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
                }

                Card card = new Card(((EditText) view.findViewById(R.id.card_number_edit_txt)).getText().toString(), getCardMonth(), getCardYear(), s.toString());

                if (card.validateCVC()) {
                    ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).setTextColor(Color.BLACK);

                } else {
                    ((EditText) view.findViewById(R.id.card_cvc_edit_txt)).setTextColor(Color.RED);
                }


                if (card.validateCard()) {
                    ((MainActivity) getActivity()).setOptionButtonEnable(true);
                } else {
                    ((MainActivity) getActivity()).setOptionButtonEnable(false);

                }
            }
        });


        return view;
    }

    @Override
    public void onOptionsButtonClicked() {
        final Card card = new Card(((EditText) getView().findViewById(R.id.card_number_edit_txt)).getText().toString().trim(), getCardMonth(), getCardYear(),
                ((EditText) getView().findViewById(R.id.card_cvc_edit_txt)).getText().toString().trim());

        if(!card.validateCard()){
            ((MainActivity) getActivity()).showAlertDialog("Error updating credit card", "Please Enter valid credit card", "Ok", null);
            return;
        }


        ((MainActivity) getActivity()).showProgressDialog();

        new Stripe().createToken(card, Constants.STRIPE_PUBLISH_KEY_TEST, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                ((MainActivity) getActivity()).closeProgerssDialog();
                String message = "There was an error. Please try again";

                if (error.getMessage() != null) {
                    message = ">>>>"+error.getMessage();
                }

                ((MainActivity) getActivity()).showAlertDialog("Error updating credit card", message, "Ok", null);
            }

            @Override
            public void onSuccess(Token token) {
                if (token != null) {
                    validatedCreditCard = card;
                    saveCreditCard(token.getId());
                } else {
                    ((MainActivity) getActivity()).closeProgerssDialog();
                    ((MainActivity) getActivity()).showAlertDialog("Error updating credit card", "There was an unknown error. Please try again.", "Ok", null);


                }


            }
        });
    }

    private void saveCreditCard(final String cardToken){
        Log.d("TEST", "=====>saveCreditCard>>>"+ParseUser.getCurrentUser());

        if(ParseUser.getCurrentUser() != null){
            ParseObject clientObj = ParseUser.getCurrentUser().getParseObject("client");
            Log.d("TEST", "=====>saveCreditCard>>>"+clientObj);
            if(clientObj != null){
                Map<String, Object> cardData = new LinkedHashMap<String, Object>();
                cardData.put("clientId", clientObj.getObjectId());
                cardData.put("stripeToken", cardToken);
                ParseCloud.callFunctionInBackground("updatePayment", cardData, this);

            }
        }
    }


    @Override
    public void done(Object object, com.parse.ParseException e) {

        Log.d("TEST", "=====>saveCreditCard complete");
        ((MainActivity) getActivity()).closeProgerssDialog();

        if(e == null){
            if(ParseUser.getCurrentUser() != null) {
                ParseObject clientObj = ParseUser.getCurrentUser().getParseObject("client");
                clientObj.put("stripeFour", validatedCreditCard.getLast4());
                clientObj.saveInBackground();

                UserProfileInfoFragment userProfileFragment = (UserProfileInfoFragment) getFragmentManager().findFragmentByTag(FragmentHolder.FragmentTags.UserProfileFragmentTag.name());
                if(userProfileFragment != null){
                    userProfileFragment.updateCreditCardInfo(validatedCreditCard.getLast4());
                }


                FragmentHolder.onBackPressed(getActivity());
            }

        }else{
            ((MainActivity) getActivity()).showAlertDialog("Error saving credit card", "Your credit card could not be updated. Please try again.\n"+e.getMessage(), "Ok", null);

        }
    }




    @Override
    protected void onFragmentResumed() {
        ((MainActivity) getActivity()).setStatusBarColor(R.color.topbar_clr);
        ((MainActivity) getActivity()).setTitle("");
        ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity) getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setNavigationText("Close");
        ((MainActivity) getActivity()).setOptionsButtonLabel("Save");
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.NavigationType);
        ((MainActivity) getActivity()).setActionBarBgColor(R.color.topbar_clr);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.black), CustomActionBar.TitleType);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.OptionType);
        ((MainActivity) getActivity()).setActionbarButtonClickListener(this);
        ((MainActivity) getActivity()).setOptionButtonEnable(false);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.GONE);



    }



    private int getCardMonth(){
        String[] date = ((EditText) getView().findViewById(R.id.card_date_edit_text)).getText().toString().split("/");
        int month = 0;
        try {
            if (date.length >= 1) {
                month = Integer.parseInt(date[0]);
            }
        }catch (Exception e){
        }

        return month;
    }


    private int getCardYear(){
        String[] date = ((EditText) getView().findViewById(R.id.card_date_edit_text)).getText().toString().split("/");
        int year = 0;
        try{
            if(date.length == 2){
                year = Integer.parseInt(date[1]);
            }
        }catch (Exception e){
        }

        return year;
    }



    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
