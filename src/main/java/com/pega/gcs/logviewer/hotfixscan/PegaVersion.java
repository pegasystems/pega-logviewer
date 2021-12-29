/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.util.ArrayList;
import java.util.List;

public class PegaVersion {

    private String version;

    private String[] altNames;

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final PegaVersion PEGA_0601_SP1     = new PegaVersion("0601_SP1"     , "06-01_SP1"    );
    public static final PegaVersion PEGA_0601_SP2     = new PegaVersion("0601_SP2"     , "06-01_SP2"    );
    public static final PegaVersion PEGA_0602_SP1     = new PegaVersion("0602_SP1"     , "06-02_SP1"    );
    public static final PegaVersion PEGA_0602_SP1_ML1 = new PegaVersion("0602_SP1_ML1" , "06-02_SP1_ML1");
    public static final PegaVersion PEGA_0602_SP2     = new PegaVersion("0602_SP2"     , "06-02_SP2"    );
    public static final PegaVersion PEGA_0602_SP2_ML1 = new PegaVersion("0602_SP2_ML1" , "06-02_SP2_ML1");
    public static final PegaVersion PEGA_0603_SP1     = new PegaVersion("0603_SP1"     , "06-03_SP1"    );
    public static final PegaVersion PEGA_0710_ML2     = new PegaVersion("0710_ML2"     , "07-10_ML2"    );
    public static final PegaVersion PEGA_0710_ML3     = new PegaVersion("0710_ML3"     , "07-10_ML3"    );
    public static final PegaVersion PEGA_0710_ML4     = new PegaVersion("0710_ML4"     , "07-10_ML4"    );
    public static final PegaVersion PEGA_0710_ML5     = new PegaVersion("0710_ML5"     , "07-10_ML5"    );
    public static final PegaVersion PEGA_0710_ML6     = new PegaVersion("0710_ML6"     , "07-10_ML6"    );
    public static final PegaVersion PEGA_0710_ML7     = new PegaVersion("0710_ML7"     , "07-10_ML7"    );
    public static final PegaVersion PEGA_0710_ML8     = new PegaVersion("0710_ML8"     , "Pega 7.1.8"   );
    public static final PegaVersion PEGA_0710_ML9     = new PegaVersion("0710_ML9"     , "Pega 7.1.9"   );
    public static final PegaVersion PEGA_0710_ML10    = new PegaVersion("0710_ML10"    , "Pega 7.1.10"  );
    public static final PegaVersion PEGA_0720_ML0     = new PegaVersion("0720_ML0"     , "Pega 7.2.0"   );
    public static final PegaVersion PEGA_0720_ML1     = new PegaVersion("0720_ML1"     , "Pega 7.2.1"   );
    public static final PegaVersion PEGA_0720_ML2     = new PegaVersion("0720_ML2"     , "Pega 7.2.2"   );
    public static final PegaVersion PEGA_7_3_0        = new PegaVersion("7.3.0"        , "Pega 7.3.0"   );
    public static final PegaVersion PEGA_7_3_1        = new PegaVersion("7.3.1"        , "Pega 7.3.1"   );
    public static final PegaVersion PEGA_7_21_1       = new PegaVersion("7.21.1"       , ""             );
    public static final PegaVersion PEGA_7_22         = new PegaVersion("7.22"         , ""             );
    public static final PegaVersion PEGA_7_21         = new PegaVersion("7.21"         , ""             );
    public static final PegaVersion PEGA_0602         = new PegaVersion("0602"         , ""             );
    public static final PegaVersion PEGA_0601         = new PegaVersion("0601"         , ""             );
    public static final PegaVersion PEGA_0603         = new PegaVersion("0603"         , ""             );
    public static final PegaVersion PEGA_07_11        = new PegaVersion("07.11"        , ""             );
    public static final PegaVersion PEGA_6_1_SP2      = new PegaVersion("6.1 SP2"      , ""             );
    public static final PegaVersion PEGA_7_17         = new PegaVersion("7.17"         , ""             );
    public static final PegaVersion PEGA_7_31         = new PegaVersion("7.31"         , ""             );
    // CHECKSTYLE:ON
    // @formatter:on

    private static final List<PegaVersion> pegaVersionList;

    static {
        pegaVersionList = new ArrayList<>();

        pegaVersionList.add(PEGA_0710_ML2);
        pegaVersionList.add(PEGA_0710_ML3);
        pegaVersionList.add(PEGA_0710_ML4);
        pegaVersionList.add(PEGA_0710_ML5);
        pegaVersionList.add(PEGA_0710_ML6);
        pegaVersionList.add(PEGA_0710_ML7);
        pegaVersionList.add(PEGA_0710_ML8);
        pegaVersionList.add(PEGA_0710_ML9);
        pegaVersionList.add(PEGA_0710_ML10);
        pegaVersionList.add(PEGA_0720_ML0);
        pegaVersionList.add(PEGA_0720_ML1);
        pegaVersionList.add(PEGA_0720_ML2);
        pegaVersionList.add(PEGA_0601_SP1);
        pegaVersionList.add(PEGA_0601_SP2);
        pegaVersionList.add(PEGA_0602_SP1);
        pegaVersionList.add(PEGA_0602_SP1_ML1);
        pegaVersionList.add(PEGA_0602_SP2);
        pegaVersionList.add(PEGA_0602_SP2_ML1);
        pegaVersionList.add(PEGA_0603_SP1);
        pegaVersionList.add(PEGA_7_22);
        pegaVersionList.add(PEGA_7_21);
        pegaVersionList.add(PEGA_0602);
        pegaVersionList.add(PEGA_0601);
        pegaVersionList.add(PEGA_0603);
        pegaVersionList.add(PEGA_07_11);
        pegaVersionList.add(PEGA_6_1_SP2);
        pegaVersionList.add(PEGA_7_3_0);
        pegaVersionList.add(PEGA_7_21_1);
        pegaVersionList.add(PEGA_7_3_1);
        pegaVersionList.add(PEGA_7_17);
        pegaVersionList.add(PEGA_7_31);
    }

    private PegaVersion(String version, String... altNames) {

        this.version = version;

        this.altNames = altNames;
    }

    public String getVersion() {
        return version;
    }

    private String[] getAltNames() {
        return altNames;
    }

    public static PegaVersion getPegaVersion(String pvStr) {

        PegaVersion pegaVersion = null;

        for (PegaVersion pv : pegaVersionList) {

            boolean match = false;

            String[] altNames = pv.getAltNames();

            for (String altName : altNames) {

                if (pvStr.equals(altName)) {
                    match = true;
                    break;
                }
            }

            if (match) {
                pegaVersion = pv;
                break;
            }

        }

        return pegaVersion;
    }
}
