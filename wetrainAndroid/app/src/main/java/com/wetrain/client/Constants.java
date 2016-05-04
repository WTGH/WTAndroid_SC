package com.wetrain.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 12/26/15.
 */
public class Constants {

    //Local Parse Key
    /*public static final String PARSE_APPLICATION_ID = "SBgeyUCbk0pnjR4OXdrMAjCW43Yp5ShTWBMnWWBJ";
    public static final String PARSE_CLIENT_KEY = "CQ2IBfn5GgN3161ITfHIK5ORmKnpgzIwUexZfuG7";*/

    //Parse Dev Keys
    public static final String PARSE_APPLICATION_ID = "PSgTQ91JT6JQUjmm5XmdylwCMPzckertjqul6AKL";
    public static final String PARSE_CLIENT_KEY = "EwYejFi8NGJ8XSLLlEfv4XPgSzPksGzeIO94Ljo1";


    public static final String STRIPE_PUBLISH_KEY_TEST = "pk_test_44V2WNWqf37KXEnaJE2CM5rf";
    public static final String STRIPE_PUBLISH_KEY_PROD = "pk_live_egDYTQMRk9mIkZYQPp0YtwFn";

    public static final boolean TESTING = true;



    public static final int UPDATE_REQUEST_INTERVAL = 10000;


    public static final double PHILADELPHIA_LAT = 39.950956;
    public static final double PHILADELPHIA_LON = -75.165741;
    public static final double SERVICE_RANGE_METERS = 8046; // 5 mile radius
    public static final double SCHEDULE_SERVICE_RANGE_METERS = 32186; // 20 mile radius

    public static final boolean MOCK_LOCATION_ENABLED = false;



    public static final int SCHEDULE_NOTIFICATION_INTERVAL = 90;



    public static final String FIRST_TIME_INSTALL = "FirstTimeInstall";

    public static enum WORKOUT_REQUEST_STATE{
        NoRequest{
            @Override
            public String toString() {
                return "none";
            }
        },
        Searching {
            @Override
            public String toString() {
                return "requested";
            }
        },
        Matched {
            @Override
            public String toString() {
                return "matched";
            }
        },
        Training {
            @Override
            public String toString() {
                return "training";
            }
        },
        Cancelled {
            @Override
            public String toString() {
                return "cancelled";
            }
        },
        Complete {
            @Override
            public String toString() {
                return "complete";
            }
        }

    }

    public static enum SCHEDULE_STATE {

        Created {
            @Override
            public String toString() {
                return "created";
            }
        },
        Edited {
            @Override
            public String toString() {
                return "edited";
            }
        },
        Searching {
            @Override
            public String toString() {
                return "requested";
            }
        },
        GoingOn {
            @Override
            public String toString() {
                return "goingOn";
            }
        },
        Canceled {
            @Override
            public String toString() {
                return "canceled";
            }
        },
        Expired {
            @Override
            public String toString() {
                return "expired";
            }
        },
        SelfConfirmed {
            @Override
            public String toString() { return "selfConfirmed";  }
        }
    }


    public static enum RATING_STATE {
        Rated {
            @Override
            public String toString() {
                return "rated";
            }
        },
        MayBeLater{
            @Override
            public String toString() {
                return "maybeLater";
            }
        }
    }


    public static final List<String> WORKOUT_TITLES = Arrays.asList(new String[] {"Liposuction",
                                                                                "Mobi-Fit",
                                                                                "The BLT",
                                                                                "Belly Busters",
                                                                                "Sports Endurance",
                                                                                "Shred Factory"});


    public static final String GET_WORKOUT_SUB_TITLE(String woType){
        switch (woType){
            case "Liposuction":
                return "Weight Loss";
            case "Shred Factory":
                return "Strength";
            case "Belly Busters":
                return "Core";
            case "Mobi-Fit":
                return "Flexibility";
            case "Sports Endurance":
                return "Cardio";
            case "The BLT":
                return "Butt, Legs & Tummy";
            case "Healthy Heart":
                return "Cardio";
            case "Tyrannosaurus Flex":
                return "Strength and Hypertrophy";
        }

        return "";
    }

    public static final int GET_WORKOUT_DRAWABLE_ICON(String woType){
        switch (woType){
            case "Healthy Heart":
                return R.drawable.exercise_healthy_heart;
            case "Liposuction":
                return R.drawable.exercise_lipo;
            case "Mobi-Fit":
                return R.drawable.exercise_mobi_fit;
            case "The BLT":
                return R.drawable.exercise_blt;
            case "Belly Busters":
                return R.drawable.exercise_belly_busters;
            case "Tyrannosaurus Flex":
                return R.drawable.exercise_tflex;
            case "Sports Endurance":
                return R.drawable.exercise_sports_endurance;
            case "Shred Factory":
                return R.drawable.exercise_shred_factory;


        }

        return 0;
    }

    public static final int GET_WORKOUT_DESC_DRAWABLE_ICON(String woType){
        switch (woType){
            case "Healthy Heart":
                return R.drawable.desc_healthyheart;
            case "Liposuction":
                return R.drawable.desc_lyposuction;
            case "Mobi-Fit":
                return R.drawable.desc_mobifit;
            case "The BLT":
                return R.drawable.desc_theblt;
            case "Belly Busters":
                return R.drawable.desc_bellybusters;
            case "Tyrannosaurus Flex":
                return R.drawable.desc_tyranasourus;
            case "Sports Endurance":
                return R.drawable.desc_sports_endurance;
            case "Shred Factory":
                return R.drawable.desc_shred_factory;


        }

        return 0;
    }



    public static final String GET_WORKOUT_DESC(String woType) {
        switch (woType) {
            case "Healthy Heart":
                return "Cardiovascular fitness is one of the most critical components of long term health. With this workout, you should expect an aerobic based workout focused on cardiovascular conditioning.\nWith Healthy Heart you will be moving from exercise to exercise with a high repetition count to keep your heart rate within at least 60% of your maximum target heart rate!";
            case "Liposuction":
                return "Geared towards weight loss, the Liposuction workout will have you sizzling and chiseling the pounds away! Expect workouts involving body weight movements designed to elevate your heart rate and kick start your metabolism!";
            case "Mobi-Fit":
                return "Flexibility is important for your health and prevents future injuries. The Mobi-Fit workout is similar to a high-intensity yoga style workout. It features both static and dynamic stretching along with full range of motion exercises to improve muscle flexibility!";
            case "The BLT":
                return "A staff favorite! The BLT focuses on toning three core muscle groups: your butt, legs and tummy (hence the name!). Expect body weight exercises featuring isometric holds combined with light cardio to get your BLT in shape!";
            case "Belly Busters":
                return "Tone, tighten and ripple! The Belly Buster is a core blasting workout designed to give you six-pack abs and improve your functional stability. As an added bonus, a strong core helps reduce lower back pain and improve posture!";
            case "Tyrannosaurus Flex":
                return "Time to put on some mad gains! This is a muscle builder, and you can expect resitance based training for this workout with high intensity intervals designed to blast your muscles!";
            case "Sports Endurance":
                return "Are you an athlete looking to improve your performance on the field? The Sports Endurance workout features interval training combined with explosive movements. This workout will improve agility, endurance, speed, and overall athletic performance!";
            case "Shred Factory":
                return "A body toning workout focused on firming up muscles and giving you definition. You won't stop moving in this high-intensity circuit workout, getting a full body burn and leaving no muscle behind!";
        }

        return "";
    }


    public static final String TERMS_CONDITIONS = "I have volunteered to participate in the Services (the “Services” as defined in the Terms and Conditions Agreement) provided to me by WeTrain, LLC  (“Company”) through the Company’s third party providers of such Services, which may include, but may not be limited to, resistance training, aerobic training, mobility training, or cardiovascular exercise. In consideration of the Company’s agreement to provide the Services through the Company’s third party providers, I do here now and forever release and discharge and hereby hold harmless the Company and their third party providers, respective agents, heirs, assigns, contractors, and employees from any and all claims, demands, damages, rights of action or causes of action, present or future, arising out of or connected with the Services and/or my participation in this or any exercise program including any injuries resulting there from.\n" +
            " \n" +
            "THIS WAIVER AND RELEASE OF LIABILITY INCLUDES, WITHOUT LIMITATION, INJURIES WHICH MAY OCCUR AS A RESULT OF (1) EQUIPMENT BELONGING TO THIRD PARTY PROVIDERS OR TO MYSELF THAT MAY MALFUNCTION OR BREAK; (2) ANY SLIP, FALL, DROPPING OF EQUIPMENT; (3) AND/OR NEGLIGENT INSTRUCTION OR SUPERVISION.\n" +
            " \n" +
            "I am aware that there are significant risks involved in all aspects of physical training whether or not requiring the use of exercise equipment. These risks include, but are not limited to: falls which can result in serious injury or death; injury or death due to negligence on the part of myself, my personal trainer, or other people around me; strains and sprains; abnormal changes in blood pressure, fainting, and a remote risk of heart attack, stroke, other serious disability or death. I am voluntarily participating in these activities and using equipment with full knowledge, understanding and appreciation of the dangers involved. I am aware that any of these above mentioned risks may result in serious injury or death to myself and or my partner(s). I willingly assume full responsibility for the risks that I am exposing myself to and accept full responsibility for any injury or death that may result from participation in any activity or Service provided by the Company through its third party providers. \n" +
            " \n" +
            "I have been advised that an examination by a physician should be obtained by anyone prior to commencing a fitness and/or exercise program, or initiating a substantial change in the amount of regular physical activity performed. If I have chosen not to obtain a physician’s consent prior to beginning this fitness program with the Company’s third party providers, I hereby agree that I am doing so solely at my own risk. In any event, I acknowledge and agree that I assume the risks associated with any and all fitness related activities and/or exercises in which I participate.\n" +
            " \n" +
            "I ACKNOWLEDGE THAT I HAVE NO PHYSICAL IMPAIRMENT, INJURIES, OR ILLNESSES THAT WILL ENDANGER MYSELF OR OTHERS. I ACKNOWLEDGE THAT I HAVE THOROUGHLY READ THIS FORM IN ITS ENTIRETY AND FULLY UNDERSTAND THAT IT IS A RELEASE OF LIABILITY. BY THE EXECUTION OF THIS AGREEMENT, I ACCEPT AND ASSUME FULL RESPONSIBILITY FOR ANY AND ALL INJURIES, DAMAGES (BOTH ECONOMIC AND NON-ECONOMIC), AND LOSSES OF ANY TYPE, WHICH MAY OCCUR TO ME OR MY GUEST, AND HEREBY FULLY AND FOREVER RELEASE AND DISCHARGE THE COMPANY, ITS INSURERS, THIRD PARTY PROVIDERS, EMPLOYEES, OFFICERS, DIRECTORS, AND ASSOCIATES, FROM ANY AND ALL CLAIMS, DEMANDS, DAMAGES, RIGHTS OF ACTION, OR CAUSES OF ACTION, PRESENT OR FUTURE, WHETHER THE SAME BE KNOWN OR UNKNOWN, ANTICIPATED, OR UNANTICIPATED, RESULTING FROM OR ARISING OUT OF THE USE OF SAID SERVICES WHICH ARE PROVIDED BY THE COMPANY OR ITS THRID PARTY PROVIDERS.\n" +
            " \n" +
            "This form is an important legal document that explains the risks you are assuming by beginning an exercise program. It is critical that you have read and understand this document completely. If you do not understand any part of this document, it is your ultimate responsibility to ask for clarification prior to participating in the Services.\n" +
            "………………………………………………………………………………………………\n" +
            " \n" +
            "1. CONTRACTUAL RELATIONSHIP\n" +
            " \n" +
            "These Terms of Use (“Terms”) govern the access or use by you, an individual, from within the United States and its territories and possessions of applications, websites, content, products, and services (the “Services”) made available in the United States and its territories and possessions by WeTrain, LLC and its subsidiaries and affiliates (collectively, “WeTrain”). PLEASE READ THESE TERMS CAREFULLY BEFORE ACCESSING OR USING THE SERVICES. In these Terms, the words “including” and “include” mean “including, but not limited to.”\n" +
            " \n" +
            "Your access and use of the Services constitutes your agreement to be bound by these Terms, which establishes a contractual relationship between you and WeTrain. If you do not agree to these Terms, you may not access or use the Services. These Terms expressly supersede prior agreements or arrangements with you. WeTrain may immediately terminate these Terms or any Services with respect to you, or generally cease offering or deny access to the Services or any portion thereof, at any time for any reason.\n" +
            " \n" +
            "Supplemental terms may apply to certain Services, such as policies for a particular event, activity or promotion, and such supplemental terms will be disclosed to you in connection with the applicable Service(s). Supplemental terms are in addition to, and shall be deemed a part of, the Terms for the purposes of the applicable Service(s). Supplemental terms shall prevail over these Terms in the event of a conflict with respect to the applicable Services.\n" +
            " \n" +
            "WeTrain may amend the Terms related to the Services from time to time. Amendments will be effective upon WeTrain’s posting of such updated Terms at this location or the amended policies or supplemental terms on the applicable Service(s). Your continued access or use of the Services after such posting constitutes your consent to be bound by the Terms, as amended. \n" +
            " \n" +
            "2. THE SERVICES\n" +
            " \n" +
            "The Services constitute a technology platform that enables users of WeTrain’s mobile applications or websites provided as part of the Services (each, an “Application”) to arrange and schedule personal training and/or fitness services with third party providers of such services, including independent third party personal training providers and third party fitness providers under agreement with WeTrain or certain of WeTrain’s affiliates (“Third Party Providers”). YOU ACKNOWLEDGE THAT WETRAIN DOES NOT PROVIDE PERSONAL TRAINING OR FITNESS SERVICES OR FUNCTION AS AN EXERCISE OR FITNESS FACILITY.\n" +
            " \n" +
            "LICENSE.\n" +
            "Subject to your compliance with these Terms, WeTrain grants you a limited, non-exclusive, non-sublicensable, revocable, non-transferrable license to:   access and use the Applications on your personal device solely in connection with your use of the Services; and (ii) access and use any content, information and related materials that may be made available through the Services, in each case solely for your personal, noncommercial use. Any rights not expressly granted herein are reserved by WeTrain and WeTrain’s licensors.\n" +
            " \n" +
            "RESTRICTIONS.\n" +
            "You may not:   remove any copyright, trademark or other proprietary notices from any portion of the Services; (ii) reproduce, modify, prepare derivative works based upon, distribute, license, lease, sell, resell, transfer, publicly display, publicly perform, transmit, stream, broadcast or otherwise exploit the Services except as expressly permitted by WeTrain; (iii) decompile, reverse engineer or disassemble the Services except as may be permitted by applicable law; (iv) link to, mirror or frame any portion of the Services; (v) cause or launch any programs or scripts for the purpose of scraping, indexing, surveying, or otherwise data mining any portion of the Services or unduly burdening or hindering the operation and/or functionality of any aspect of the Services; or (vi) attempt to gain unauthorized access to or impair any aspect of the Services or its related systems or networks.\n" +
            " \n" +
            "THIRD PARTY SERVICES AND CONTENT.\n" +
            "The Services may be made available or accessed in connection with third party services and content (including advertising) that WeTrain does not control. You acknowledge that different terms of use and privacy policies may apply to your use of such third party services and content. WeTrain does not endorse such third party services and content and in no event shall WeTrain be responsible or liable for any products or services of such third party providers. Additionally, Apple Inc., Google, Inc., Microsoft Corporation or BlackBerry Limited will be a third-party beneficiary to this contract if you access the Services using Applications developed for Apple iOS, Android, Microsoft Windows, or Blackberry-powered mobile devices, respectively. These third party beneficiaries are not parties to this contract and are not responsible for the provision or support of the Services in any manner. Your access to the Services using these devices is subject to terms set forth in the applicable third party beneficiary's terms of service.\n" +
            " \n" +
            "OWNERSHIP.\n" +
            "The Services and all rights therein are and shall remain WeTrain’s property or the property of WeTrain’s licensors. Neither these Terms nor your use of the Services convey or grant to you any rights:   in or related to the Services except for the limited license granted above; or (ii) to use or reference in any manner WeTrain’s company names, logos, product and service names, trademarks or services marks or those of WeTrain’s licensors.\n" +
            " \n" +
            "3. YOUR USE OF THE SERVICES\n" +
            " \n" +
            "USER ACCOUNTS.\n" +
            "In order to use most aspects of the Services, you must register for and maintain an active personal user Services account (\"Account\"). You must be at least 18 years of age, or the age of legal majority in your jurisdiction (if different than 18), to obtain an Account. Account registration requires you to submit to WeTrain certain personal information, such as your name, address, mobile phone number, age, past and current medical history, and desired fitness goals, as well as at least one valid payment method (either a credit card or accepted payment partner). You agree to maintain accurate, complete, and up-to-date information in your Account. Your failure to maintain accurate, complete, and up-to-date Account information, including having an invalid or expired payment method on file, or failure to update your medical history, may result in your inability to access and use the Services or WeTrain’s termination of this Agreement with you. You are responsible for all activity that occurs under your Account, and you agree to maintain the security and secrecy of your Account username and password at all times. Unless otherwise permitted by WeTrain in writing, you may only possess one Account.\n" +
            " \n" +
            "USER REQUIREMENTS AND CONDUCT.\n" +
            "The Service is not available for use by persons under the age of 18. You may not authorize third parties to use your Account, and you may not allow persons under the age of 18 to receive personal training or fitness services from Third Party Providers unless they are accompanied by you. You may not assign or otherwise transfer your Account to any other person or entity. You agree to comply with all applicable laws when using the Services. You will not in your use of the Services cause nuisance, annoyance, inconvenience, or property damage, whether to the Third Party Provider or any other party. In certain instances you may be asked to provide proof of identity to access or use the Services, and you agree that you may be denied access to or use of the Services if you refuse to provide proof of identity.\n" +
            " \n" +
            "TEXT MESSAGING.\n" +
            "By creating an Account, you agree that the Services may send you informational text (SMS) messages as part of the normal business operation of your use of the Services. You may opt-out of receiving text (SMS) messages from WeTrain at any time by emailing support@wetrainphilly.com. You acknowledge that opting out of receiving text (SMS) messages may impact your use of the Services.\n" +
            " \n" +
            "PROMOTIONAL CODES.\n" +
            "WeTrain may, in WeTrain’s sole discretion, create promotional codes that may be redeemed for Account credit, or other features or benefits related to the Services and/or a Third Party Provider's services, subject to any additional terms that WeTrain establishes on a per promotional code basis (“Promo Codes”). You agree that Promo Codes:   must be used for the intended audience and purpose, and in a lawful manner; (ii) may not be duplicated, sold or transferred in any manner, or made available to the general public (whether posted to a public form or otherwise), unless expressly permitted by WeTrain; (iii) may be disabled by WeTrain at any time for any reason without liability to WeTrain; (iv) may only be used pursuant to the specific terms that WeTrain establishes for such Promo Code; (v) are not valid for cash; and (vi) may expire prior to your use. WeTrain reserves the right to withhold or deduct credits or other features or benefits obtained through the use of Promo Codes by you or any other user in the event that WeTrain determines or believes that the use or redemption of the Promo Code was in error, fraudulent, illegal, or in violation of the applicable Promo Code terms or these Terms.\n" +
            " \n" +
            "USER PROVIDED CONTENT.\n" +
            "WeTrain may, in WeTrain’s sole discretion, permit you from time to time to submit, upload, publish or otherwise make available to WeTrain through the Services textual, audio, and/or visual content and information, including commentary and feedback related to the Services, initiation of support requests, and submission of entries for competitions and promotions (\"User Content\"). Any User Content provided by you remains your property. However, by providing User Content to WeTrain, you grant WeTrain a worldwide, perpetual, irrevocable, transferrable, royalty-free license, with the right to sublicense, to use, copy, modify, create derivative works of, distribute, publicly display, publicly perform, and otherwise exploit in any manner such User Content in all formats and distribution channels now known or hereafter devised (including in connection with the Services and WeTrain’s business and on third-party sites and services), without further notice to or consent from you, and without the requirement of payment to you or any other person or entity.\n" +
            " \n" +
            "You represent and warrant that:   you either are the sole and exclusive owner of all User Content or you have all rights, licenses, consents and releases necessary to grant WeTrain the license to the User Content as set forth above; and (ii) neither the User Content nor your submission, uploading, publishing or otherwise making available of such User Content nor WeTrain’s use of the User Content as permitted herein will infringe, misappropriate or violate a third party's intellectual property or proprietary rights, or rights of publicity or privacy, or result in the violation of any applicable law or regulation.\n" +
            " \n" +
            "You agree to not provide User Content that is defamatory, libelous, hateful, violent, obscene, pornographic, unlawful, or otherwise offensive, as determined by WeTrain in its sole discretion, whether or not such material may be protected by law. WeTrain may, but shall not be obligated to, review, monitor, or remove User Content, at WeTrain’s sole discretion and at any time and for any reason, without notice to you.\n" +
            " \n" +
            "NETWORK ACCESS AND DEVICES.\n" +
            "You are responsible for obtaining the data network access necessary to use the Services. Your mobile network's data and messaging rates and fees may apply if you access or use the Services from a wireless-enabled device. You are responsible for acquiring and updating compatible hardware or devices necessary to access and use the Services and Applications and any updates thereto. WeTrain does not guarantee that the Services, or any portion thereof, will function on any particular hardware or devices. In addition, the Services may be subject to malfunctions and delays inherent in the use of the Internet and electronic communications.\n" +
            " \n" +
            "4. PAYMENT\n" +
            " \n" +
            "You understand that use of the Services may result in charges to you for the services or goods you receive from a Third Party Provider (“Charges”). After you have received services or goods obtained through your use of the Service, WeTrain will facilitate your payment of the applicable Charges on behalf of the Third Party Provider, as such Third Party Provider's limited payment collection agent. Payment of the Charges in such manner shall be considered the same as payment made directly by you to the Third Party Provider. Charges will be inclusive of applicable taxes where required by law. Charges paid by you are final and non-refundable, unless otherwise determined by WeTrain. \n" +
            " \n" +
            "All Charges are due immediately and payment will be facilitated by WeTrain using the preferred payment method designated in your Account. If your primary Account payment method is determined to be expired, invalid or otherwise not able to be charged, you agree that WeTrain may, as the Third Party Provider's limited payment collection agent, use a secondary payment method in your Account, if available.\n" +
            " \n" +
            "As between you and WeTrain, WeTrain reserves the right to establish, remove and/or revise Charges for any or all services or goods obtained through the use of the Services at any time in WeTrain’s sole discretion. WeTrain may from time to time provide certain users with promotional offers and discounts that may result in different amounts charged for the same or similar services or goods obtained through the use of the Services, and you agree that such promotional offers and discounts, unless also made available to you, shall have no bearing on your use of the Services or the Charges applied to you. You may elect to cancel your request for services or goods from a Third Party Provider at any time prior to such Third Party Provider's arrival, in which case you may be charged a cancellation fee. This payment structure is intended to fully compensate the Third Party Provider for the services or goods provided. WeTrain does not designate any portion of your payment as a tip or gratuity to the Third Party Provider. Any representation by WeTrain to the effect that tipping is “voluntary,” “not required,” and/or “included” in the payments you make for services or goods provided is not intended to suggest that WeTrain provides any additional amounts, beyond those described above, to the Third Party Provider. You understand and agree that, while you are free to provide additional payment as a gratuity to any Third Party Provider who provides you with services or goods obtained through the Service, you are under no obligation to do so. Gratuities are voluntary. After you have received services or goods obtained through the Service, you will have the opportunity to rate your experience and leave additional feedback about your Third Party Provider.\n" +
            " \n" +
            "5. DISCLAIMERS; LIMITATION OF LIABILITY; INDEMNITY.\n" +
            " \n" +
            "DISCLAIMER.\n" +
            "THE SERVICES ARE PROVIDED “AS IS” AND “AS AVAILABLE.” WETRAIN DISCLAIMS ALL REPRESENTATIONS AND WARRANTIES, EXPRESS, IMPLIED, OR STATUTORY, NOT EXPRESSLY SET OUT IN THESE TERMS, INCLUDING THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN ADDITION, WETRAIN MAKES NO REPRESENTATION, WARRANTY, OR GUARANTEE REGARDING THE RELIABILITY, TIMELINESS, QUALITY, SUITABILITY, OR AVAILABILITY OF THE SERVICES OR ANY SERVICES OR GOODS REQUESTED THROUGH THE USE OF THE SERVICES, OR THAT THE SERVICES WILL BE UNINTERRUPTED OR ERROR-FREE. WETRAIN DOES NOT GUARANTEE THE QUALITY, SUITABILITY, SAFETY OR ABILITY OF THIRD PARTY PROVIDERS. YOU AGREE THAT THE ENTIRE RISK ARISING OUT OF YOUR USE OF THE SERVICES, AND ANY SERVICE OR GOOD REQUESTED IN CONNECTION THEREWITH, REMAINS SOLELY WITH YOU, TO THE MAXIMUM EXTENT PERMITTED UNDER APPLICABLE LAW.\n" +
            " \n" +
            "LIMITATION OF LIABILITY.\n" +
            "WETRAIN SHALL NOT BE LIABLE FOR INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, LOST DATA, PERSONAL INJURY, OR PROPERTY DAMAGE RELATED TO, IN CONNECTION WITH, OR OTHERWISE RESULTING FROM ANY USE OF THE SERVICES, EVEN IF WETRAIN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. WETRAIN SHALL NOT BE LIABLE FOR ANY DAMAGES, LIABILITY OR LOSSES ARISING OUT OF:   YOUR USE OF OR RELIANCE ON THE SERVICES OR YOUR INABILITY TO ACCESS OR USE THE SERVICES; OR (ii) ANY TRANSACTION OR RELATIONSHIP BETWEEN YOU AND ANY THIRD PARTY PROVIDER, EVEN IF WETRAIN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. WETRAIN SHALL NOT BE LIABLE FOR DELAY OR FAILURE IN PERFORMANCE RESULTING FROM CAUSES BEYOND WETRAIN’S REASONABLE CONTROL. IN NO EVENT SHALL WETRAIN’S TOTAL LIABILITY TO YOU IN CONNECTION WITH THE SERVICES FOR ALL DAMAGES, LOSSES AND CAUSES OF ACTION EXCEED ONE HUNDRED U.S. DOLLARS (US $100).\n" +
            " \n" +
            "WETRAIN’S SERVICES MAY BE USED BY YOU TO REQUEST AND SCHEDULE PERSONAL TRAINING, GOODS, OR FITNESS SERVICES WITH THIRD PARTY PROVIDERS, BUT YOU AGREE THAT WETRAIN HAS NO RESPONSIBILITY OR LIABILITY TO YOU RELATED TO ANY PERSONAL TRAINING, GOODS OR FITNESS SERVICES PROVIDED TO YOU BY THIRD PARTY PROVIDERS OTHER THAN AS EXPRESSLY SET FORTH IN THESE TERMS.\n" +
            " \n" +
            "THE LIMITATIONS AND DISCLAIMER IN THIS SECTION 5 DO NOT PURPORT TO LIMIT LIABILITY OR ALTER YOUR RIGHTS AS A CONSUMER THAT CANNOT BE EXCLUDED UNDER APPLICABLE LAW.\n" +
            " \n" +
            "INDEMNITY.\n" +
            "You agree to indemnify and hold WeTrain and its officers, directors, employees, and agents harmless from any and all claims, demands, losses, liabilities, and expenses (including attorneys' fees), arising out of or in connection with:   your use of the Services or services or goods obtained through your use of the Services; (ii) your breach or violation of any of these Terms; (iii) WeTrain’s use of your User Content; or (iv) your violation of the rights of any third party, including Third Party Providers.\n" +
            " \n" +
            "6. DISPUTE RESOLUTION\n" +
            " \n" +
            "ARBITRATION.\n" +
            "You agree that any dispute, claim or controversy arising out of or relating to these Terms or the breach, termination, enforcement, interpretation or validity thereof or the use of the Services (collectively, “Disputes”) will be settled by binding arbitration between you and WeTrain, except that each party retains the right to bring an individual action in small claims court and the right to seek injunctive or other equitable relief in a court of competent jurisdiction to prevent the actual or threatened infringement, misappropriation or violation of a party's copyrights, trademarks, trade secrets, patents or other intellectual property rights. You acknowledge and agree that you and WeTrain are each waiving the right to a trial by jury or to participate as a plaintiff or class in any purported class action or representative proceeding. Further, unless both you and WeTrain otherwise agree in writing, the arbitrator may not consolidate more than one person's claims, and may not otherwise preside over any form of any class or representative proceeding. If this specific paragraph is held unenforceable, then the entirety of this \"Dispute Resolution\" section will be deemed void. Except as provided in the preceding sentence, this \"Dispute Resolution\" section will survive any termination of these Terms.\n" +
            " \n" +
            "ARBITRATION RULES AND GOVERNING LAW.\n" +
            "The arbitration will be administered by the American Arbitration Association (\"AAA\") in accordance with the Commercial Arbitration Rules and the Supplementary Procedures for Consumer Related Disputes (the \"AAA Rules\") then in effect, except as modified by this \"Dispute Resolution\" section. (The AAA Rules are available at www.adr.org/arb_med or by calling the AAA at 1-800-778-7879.) The Federal Arbitration Act will govern the interpretation and enforcement of this Section.\n" +
            " \n" +
            "ARBITRATION PROCESS.\n" +
            "A party who desires to initiate arbitration must provide the other party with a written Demand for Arbitration as specified in the AAA Rules. (The AAA provides a form Demand for Arbitration at www.adr.org/aaa/ShowPDF?doc=ADRSTG_004175. The arbitrator will be either a retired judge or an attorney licensed to practice law in the state of Pennsylvania and will be selected by the parties from the AAA's roster of consumer dispute arbitrators. If the parties are unable to agree upon an arbitrator within seven (7) days of delivery of the Demand for Arbitration, then the AAA will appoint the arbitrator in accordance with the AAA Rules.\n" +
            " \n" +
            "ARBITRATION LOCATION AND PROCEDURE.\n" +
            "Unless you and WeTrain otherwise agree, the arbitration will be conducted in Philadelphia, Pennsylvania. If your claim does not exceed $10,000, then the arbitration will be conducted solely on the basis of documents you and WeTrain submit to the arbitrator, unless you request a hearing or the arbitrator determines that a hearing is necessary. If your claim exceeds $10,000, your right to a hearing will be determined by the AAA Rules. Subject to the AAA Rules, the arbitrator will have the discretion to direct a reasonable exchange of information by the parties, consistent with the expedited nature of the arbitration.\n" +
            " \n" +
            "ARBITRATOR'S DECISION.\n" +
            "The arbitrator will render an award within the time frame specified in the AAA Rules. The arbitrator's decision will include the essential findings and conclusions upon which the arbitrator based the award. Judgment on the arbitration award may be entered in any court having jurisdiction thereof. The arbitrator's award damages must be consistent with the terms of the \"Limitation of Liability\" section above as to the types and the amounts of damages for which a party may be held liable. The arbitrator may award declaratory or injunctive relief only in favor of the claimant and only to the extent necessary to provide relief warranted by the claimant's individual claim. If you prevail in arbitration you will be entitled to an award of attorneys' fees and expenses, to the extent provided under applicable law. WeTrain will not seek, and hereby waives all rights WeTrain may have under applicable law to recover, attorneys' fees and expenses if WeTrain prevails in arbitration.\n" +
            " \n" +
            "FEES.\n" +
            "Your responsibility to pay any AAA filing, administrative and arbitrator fees will be solely as set forth in the AAA Rules. However, if your claim for damages does not exceed $75,000, WeTrain will pay all such fees unless the arbitrator finds that either the substance of your claim or the relief sought in your Demand for Arbitration was frivolous or was brought for an improper purpose (as measured by the standards set forth in Federal Rule of Civil Procedure 11(b)).\n" +
            " \n" +
            "CHANGES.\n" +
            "Notwithstanding the provisions of the modification-related provisions above, if WeTrain changes this \"Dispute Resolution\" section after the date you first accepted these Terms (or accepted any subsequent changes to these Terms), you may reject any such change by providing WeTrain written notice of such rejection by email from the email address associated with your Account to support@wetrainphilly.com within 30 days of the date such change became effective, as indicated in the \"Last update\" date above. In order to be effective, the notice must include your full name and clearly indicate your intent to reject changes to this \"Dispute Resolution\" section. By rejecting changes, you are agreeing that you will arbitrate any Dispute between you and WeTrain in accordance with the provisions of this \"Dispute Resolution\" section as of the date you first accepted these Terms (or accepted any subsequent changes to these Terms).\n" +
            " \n" +
            "7. OTHER PROVISIONS\n" +
            " \n" +
            "CHOICE OF LAW.\n" +
            "These Terms are governed by and construed in accordance with the laws of the State of Pennsylvania, U.S.A., without giving effect to any conflict of law principles.\n" +
            " \n" +
            "NOTICE.\n" +
            "WeTrain may give notice by means of a general notice on the Services, electronic mail to your email address in your Account, or by written communication sent by first class mail or pre-paid post to your address in your Account.\n" +
            " \n" +
            "GENERAL.\n" +
            "You may not assign these Terms without WeTrain’s prior written approval. WeTrain may assign these Terms without your consent to:   a subsidiary or affiliate; (ii) an acquirer of WeTrain’s equity, business or assets; or (iii) a successor by merger. Any purported assignment in violation of this section shall be void. No joint venture, partnership, employment, or agency relationship exists between you, WeTrain or any Third Party Provider as a result of this Agreement or use of the Services. If any provision of these Terms is held to be invalid or unenforceable, such provision shall be struck and the remaining provisions shall be enforced to the fullest extent under law. WeTrain’s failure to enforce any right or provision in these Terms shall not constitute a waiver of such right or provision unless acknowledged and agreed to by WeTrain in writing.";


    public static long getMinutesBetweenTwoDates(Date wSessionDate){
        long currenTtime = new Date().getTime();
        long diffMs = currenTtime - wSessionDate.getTime();
        if(currenTtime < wSessionDate.getTime()){
            diffMs = wSessionDate.getTime() - currenTtime;
        }

        long diffSec = diffMs / 1000;
        long min = diffSec / 60;
        //long sec = diffSec % 60;

        return min;
    }

}
