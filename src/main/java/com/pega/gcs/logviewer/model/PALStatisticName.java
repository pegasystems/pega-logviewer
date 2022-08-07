/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import javax.swing.SwingConstants;

public enum PALStatisticName {

    // @formatter:off
    // CHECKSTYLE:OFF
    // constant factor = 0.13
    // value = ROUNDUP(length of name / 0.13)

    // -- COUNTS --
    pxActivationCount                               ( false , 131 , SwingConstants.RIGHT ),
    pxActivityCount                                 ( false , 116 , SwingConstants.RIGHT ),
    pxADPAvoidedLoadsAsUserLoadedSynchronouslyCount ( false , 362 , SwingConstants.RIGHT ),
    pxADPLoadedCount                                ( false , 124 , SwingConstants.RIGHT ),
    pxADPLoadsUsedCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxADPLoadsWastedCount                           ( false , 162 , SwingConstants.RIGHT ),
    pxADPPagesGotWithoutWaitCount                   ( false , 224 , SwingConstants.RIGHT ),
    pxAlertCount                                    ( false , 93  , SwingConstants.RIGHT ),
    pxAutoPopulateLoadCount                         ( false , 177 , SwingConstants.RIGHT ),
    pxAutoPopulateUseCount                          ( false , 170 , SwingConstants.RIGHT ),
    pxChildReqCount                                 ( false , 116 , SwingConstants.RIGHT ),
    pxClientLoadCount                               ( false , 131 , SwingConstants.RIGHT ),
    pxCommitCount                                   ( false , 100 , SwingConstants.RIGHT ),
    pxCommitRowCount                                ( false , 124 , SwingConstants.RIGHT ),
    pxConnectCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxDBOpExceedingThresholdCount                   ( false , 224 , SwingConstants.RIGHT ),
    pxDeclarativePageBrowseCount                    ( false , 216 , SwingConstants.RIGHT ),
    pxDeclarativePageLoadCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxDeclarativePageLookupCount                    ( false , 216 , SwingConstants.RIGHT ),
    pxDeclarativePageNameCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxDeclarativeRuleReadCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxDeclarativeRulesInvokedBackgroundCount        ( false , 308 , SwingConstants.RIGHT ),
    pxDeclarativeRulesInvokedCount                  ( false , 231 , SwingConstants.RIGHT ),
    pxDeclarativeRulesLookupCount                   ( false , 224 , SwingConstants.RIGHT ),
    pxDeclExprCtxFreeUseCount                       ( false , 193 , SwingConstants.RIGHT ),
    pxDeclExprCtxSensUseCount                       ( false , 193 , SwingConstants.RIGHT ),
    pxDecryptCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxEncryptCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxExceptionCount                                ( false , 124 , SwingConstants.RIGHT ),
    pxFlowCount                                     ( false , 85  , SwingConstants.RIGHT ),
    pxFrameTransactionMapCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxFUACacheDBRetreivedCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxFUACacheMemoryHitCount                        ( false , 185 , SwingConstants.RIGHT ),
    pxFUAWriteToDBCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxIndexCount                                    ( false , 93  , SwingConstants.RIGHT ),
    pxInferGeneratedJavaCount                       ( false , 193 , SwingConstants.RIGHT ),
    pxJavaAssembleCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxJavaCompileCount                              ( false , 139 , SwingConstants.RIGHT ),
    pxJavaGenerateCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxJavaStepCount                                 ( false , 116 , SwingConstants.RIGHT ),
    pxJavaSyntaxCount                               ( false , 131 , SwingConstants.RIGHT ),
    pxLegacyRuleAPIUsedCount                        ( false , 185 , SwingConstants.RIGHT ),
    pxListRowWithFilteredStreamCount                ( false , 247 , SwingConstants.RIGHT ),
    pxListRowWithoutStreamCount                     ( false , 208 , SwingConstants.RIGHT ),
    pxListRowWithUnfilteredStreamCount              ( false , 262 , SwingConstants.RIGHT ),
    pxListWithFilteredStreamCount                   ( false , 224 , SwingConstants.RIGHT ),
    pxListWithoutStreamCount                        ( false , 185 , SwingConstants.RIGHT ),
    pxListWithUnfilteredStreamCount                 ( false , 239 , SwingConstants.RIGHT ),
    pxOtherBrowseFilterCnt                          ( false , 170 , SwingConstants.RIGHT ),
    pxOtherCount                                    ( false , 93  , SwingConstants.RIGHT ),
    pxOtherFromCacheCount                           ( false , 162 , SwingConstants.RIGHT ),
    pxOtherIOCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxParseRuleCount                                ( false , 124 , SwingConstants.RIGHT ),
    pxPassivationCount                              ( false , 139 , SwingConstants.RIGHT ),
    pxPassivationOnDemandCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxProceduralRuleReadCount                       ( false , 193 , SwingConstants.RIGHT ),
    pxPropertyReadCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxRDBIOCount                                    ( false , 93  , SwingConstants.RIGHT ),
    pxRDBRowWithoutStreamCount                      ( false , 200 , SwingConstants.RIGHT ),
    pxRDBRowWithStreamCount                         ( false , 177 , SwingConstants.RIGHT ),
    pxRDBWithoutStreamCount                         ( false , 177 , SwingConstants.RIGHT ),
    pxRDBWithStreamCount                            ( false , 154 , SwingConstants.RIGHT ),
    pxReferencePropertyUseCount                     ( false , 208 , SwingConstants.RIGHT ),
    pxRuleBrowseFilterCnt                           ( false , 162 , SwingConstants.RIGHT ),
    pxRuleCount                                     ( false , 85  , SwingConstants.RIGHT ),
    pxRuleFromCacheCount                            ( false , 154 , SwingConstants.RIGHT ),
    pxRunModelCount                                 ( false , 116 , SwingConstants.RIGHT ),
    pxRunOtherRuleCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxRunStreamCount                                ( false , 124 , SwingConstants.RIGHT ),
    pxRunStreamForControlCount                      ( false , 208 , SwingConstants.RIGHT ),
    pxRunWhenCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxSavedClipboardAfterInteractionCount           ( false , 285 , SwingConstants.RIGHT ),
    pxSavedContextAfterInteractionCount             ( false , 270 , SwingConstants.RIGHT ),
    pxServiceCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxStaticContentCount                            ( false , 154 , SwingConstants.RIGHT ),
    pxStreamWriteCount                              ( false , 139 , SwingConstants.RIGHT ),
    pxTrackedPropertyChangesCount                   ( false , 224 , SwingConstants.RIGHT ),
    pxTrackerCount                                  ( false , 108 , SwingConstants.RIGHT ),
    pxTrackerMaxCount                               ( false , 131 , SwingConstants.RIGHT ),
    pxTrackerWatchChangeCount                       ( false , 193 , SwingConstants.RIGHT ),
    pxTrackerWatchCount                             ( false , 147 , SwingConstants.RIGHT ),
    pxTrackerWatchMaxCount                          ( false , 170 , SwingConstants.RIGHT ),
    pxTrackerWatchReadCount                         ( false , 177 , SwingConstants.RIGHT ),
    pxTransientJavaAssembleCount                    ( false , 216 , SwingConstants.RIGHT ),
    pxTransientJavaCompileCount                     ( false , 208 , SwingConstants.RIGHT ),

    // -- ELAPSED --
    pxActivationDataTimeElapsed                     ( true  , 208 , SwingConstants.CENTER ),
    pxADPContextSetupTimeElapsed                    ( true  , 216 , SwingConstants.CENTER ),
    pxADPLoadActivityTimeElapsed                    ( true  , 216 , SwingConstants.CENTER ),
    pxADPPageCopyTimeElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxADPQueueWaitTimeElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxADPWaitTimeElapsed                            ( true  , 154 , SwingConstants.CENTER ),
    pxAutoPopulateLoadElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxAutoPopulateUseElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxChildWaitElapsed                              ( true  , 139 , SwingConstants.CENTER ),
    pxClientLoadElapsed                             ( true  , 147 , SwingConstants.CENTER ),
    pxCommitElapsed                                 ( true  , 116 , SwingConstants.CENTER ),
    pxConnectClientInitElapsed                      ( true  , 200 , SwingConstants.CENTER ),
    pxConnectClientResponseElapsed                  ( true  , 231 , SwingConstants.CENTER ),
    pxConnectElapsed                                ( true  , 124 , SwingConstants.CENTER ),
    pxConnectInMapReqElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxConnectOutMapReqElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxDeclarativeNtwksBuildConstElapsed             ( true  , 270 , SwingConstants.CENTER ),
    pxDeclarativeNtwksBuildHLElapsed                ( true  , 247 , SwingConstants.CENTER ),
    pxDeclarativePageBrowseElapsed                  ( true  , 231 , SwingConstants.CENTER ),
    pxDeclarativePageLoadElapsed                    ( true  , 216 , SwingConstants.CENTER ),
    pxDeclarativePageLookupElapsed                  ( true  , 231 , SwingConstants.CENTER ),
    pxDeclarativePageNameElapsed                    ( true  , 216 , SwingConstants.CENTER ),
    pxDeclarativeRulesInvokedElapsed                ( true  , 247 , SwingConstants.CENTER ),
    pxDeclarativeRulesLookupElapsed                 ( true  , 239 , SwingConstants.CENTER ),
    pxDeclRulesInvokedElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxDeclRulesLookupElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxDecryptElapsed                                ( true  , 124 , SwingConstants.CENTER ),
    pxEncryptElapsed                                ( true  , 124 , SwingConstants.CENTER ),
    pxFUACacheDBLoadElapsed                         ( true  , 177 , SwingConstants.CENTER ),
    pxFUACacheMemoryLoadElapsed                     ( true  , 208 , SwingConstants.CENTER ),
    pxFUAWriteToDBTimeElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxInferGeneratedJavaElapsed                     ( true  , 208 , SwingConstants.CENTER ),
    pxInferGeneratedJavaHLElapsed                   ( true  , 224 , SwingConstants.CENTER ),
    pxJavaAssembleElapsed                           ( true  , 162 , SwingConstants.CENTER ),
    pxJavaAssembleHLElapsed                         ( true  , 177 , SwingConstants.CENTER ),
    pxJavaCompileElapsed                            ( true  , 154 , SwingConstants.CENTER ),
    pxJavaGenerateElapsed                           ( true  , 162 , SwingConstants.CENTER ),
    pxJavaStepElapsed                               ( true  , 131 , SwingConstants.CENTER ),
    pxJavaSyntaxElapsed                             ( true  , 147 , SwingConstants.CENTER ),
    pxOtherBrowseElapsed                            ( true  , 154 , SwingConstants.CENTER ),
    pxOtherBrowseFilterElapsed                      ( true  , 200 , SwingConstants.CENTER ),
    pxOtherIOElapsed                                ( true  , 124 , SwingConstants.CENTER ),
    pxParseRuleTimeElapsed                          ( true  , 170 , SwingConstants.CENTER ),
    pxPassivationDataTimeElapsed                    ( true  , 216 , SwingConstants.CENTER ),
    pxPassivationIdleTimeElapsed                    ( true  , 216 , SwingConstants.CENTER ),
    pxRDBIOElapsed                                  ( true  , 108 , SwingConstants.CENTER ),
    pxReferencePropertyUseElapsed                   ( true  , 224 , SwingConstants.CENTER ),
    pxRuleAssemblyResearchTimeElapsed               ( true  , 254 , SwingConstants.CENTER ),
    pxRuleBrowseElapsed                             ( true  , 147 , SwingConstants.CENTER ),
    pxRuleBrowseFilterElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxRuleIOElapsed                                 ( true  , 116 , SwingConstants.CENTER ),
    pxSavedClipboardAfterInteractionElapsed         ( true  , 300 , SwingConstants.CENTER ),
    pxSavedContextAfterInteractionElapsed           ( true  , 285 , SwingConstants.CENTER ),
    pxServiceActivityElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxServiceInMapReqElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxServiceOutMapReqElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxStaticContentElapsed                          ( true  , 170 , SwingConstants.CENTER ),
    pxStreamTimeElapsed                             ( true  , 147 , SwingConstants.CENTER ),
    pxStreamWriteTimeElapsed                        ( true  , 185 , SwingConstants.CENTER ),
    pxTrackerReportTimeElapsed                      ( true  , 200 , SwingConstants.CENTER ),
    pxTrackerWatchTimeElapsed                       ( true  , 193 , SwingConstants.CENTER ),
    pxTransientJavaAssembleElapsed                  ( true  , 231 , SwingConstants.CENTER ),
    pxTransientJavaCompileElapsed                   ( true  , 224 , SwingConstants.CENTER ),

    // -- CPU --
    pxActivationDataTimeCPU                         ( true  , 177 , SwingConstants.CENTER ),
    pxADPContextSetupTimeCPU                        ( true  , 185 , SwingConstants.CENTER ),
    pxADPLoadActivityTimeCPU                        ( true  , 185 , SwingConstants.CENTER ),
    pxADPPageCopyTimeCPU                            ( true  , 154 , SwingConstants.CENTER ),
    pxADPQueueWaitTimeCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxADPWaitTimeCPU                                ( true  , 124 , SwingConstants.CENTER ),
    pxAutoPopulateLoadCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxAutoPopulateUseCPU                            ( true  , 154 , SwingConstants.CENTER ),
    pxConnectClientInitCPU                          ( true  , 170 , SwingConstants.CENTER ),
    pxConnectClientResponseElapsedCPU               ( true  , 254 , SwingConstants.CENTER ),
    pxConnectInMapReqCPU                            ( true  , 154 , SwingConstants.CENTER ),
    pxConnectOutMapReqCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxDeclarativeNtwksBuildConstCPU                 ( true  , 239 , SwingConstants.CENTER ),
    pxDeclarativeNtwksBuildHLCPU                    ( true  , 216 , SwingConstants.CENTER ),
    pxDeclarativePageBrowseCPU                      ( true  , 200 , SwingConstants.CENTER ),
    pxDeclarativePageLoadCPU                        ( true  , 185 , SwingConstants.CENTER ),
    pxDeclarativePageLookupCPU                      ( true  , 200 , SwingConstants.CENTER ),
    pxDeclarativePageNameCPU                        ( true  , 185 , SwingConstants.CENTER ),
    pxDeclarativeRulesInvokedCPU                    ( true  , 216 , SwingConstants.CENTER ),
    pxDeclarativeRulesLookupCPU                     ( true  , 208 , SwingConstants.CENTER ),
    pxDecryptCPU                                    ( true  , 93  , SwingConstants.CENTER ),
    pxEncryptCPU                                    ( true  , 93  , SwingConstants.CENTER ),
    pxFUACacheDBLoadCPU                             ( true  , 147 , SwingConstants.CENTER ),
    pxFUACacheMemoryLoadCPU                         ( true  , 177 , SwingConstants.CENTER ),
    pxFUAWriteToDBTimeCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxInferGeneratedJavaCPU                         ( true  , 177 , SwingConstants.CENTER ),
    pxInferGeneratedJavaHLCPU                       ( true  , 193 , SwingConstants.CENTER ),
    pxJavaAssembleCPU                               ( true  , 131 , SwingConstants.CENTER ),
    pxJavaAssembleHLCPU                             ( true  , 147 , SwingConstants.CENTER ),
    pxJavaCompileCPU                                ( true  , 124 , SwingConstants.CENTER ),
    pxJavaGenerateCPU                               ( true  , 131 , SwingConstants.CENTER ),
    pxJavaStepCPU                                   ( true  , 100 , SwingConstants.CENTER ),
    pxJavaSyntaxCPU                                 ( true  , 116 , SwingConstants.CENTER ),
    pxOtherBrowseCPU                                ( true  , 124 , SwingConstants.CENTER ),
    pxOtherBrowseFilterCPU                          ( true  , 170 , SwingConstants.CENTER ),
    pxOtherIOCPU                                    ( true  , 93  , SwingConstants.CENTER ),
    pxParseRuleTimeCPU                              ( true  , 139 , SwingConstants.CENTER ),
    pxPassivationDataTimeCPU                        ( true  , 185 , SwingConstants.CENTER ),
    pxProcessCPU                                    ( true  , 93  , SwingConstants.CENTER ),
    pxReferencePropertyUseCPU                       ( true  , 193 , SwingConstants.CENTER ),
    pxRuleAssemblyResearchTimeCPU                   ( true  , 224 , SwingConstants.CENTER ),
    pxRuleBrowseCPU                                 ( true  , 116 , SwingConstants.CENTER ),
    pxRuleBrowseFilterCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxRuleCPU                                       ( true  , 70  , SwingConstants.CENTER ),
    pxSavedClipboardAfterInteractionCPU             ( true  , 270 , SwingConstants.CENTER ),
    pxSavedContextAfterInteractionCPU               ( true  , 254 , SwingConstants.CENTER ),
    pxServiceActivityCPU                            ( true  , 154 , SwingConstants.CENTER ),
    pxServiceInMapReqCPU                            ( true  , 154 , SwingConstants.CENTER ),
    pxServiceOutMapReqCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxStaticContentCPU                              ( true  , 139 , SwingConstants.CENTER ),
    pxStreamTimeCPU                                 ( true  , 116 , SwingConstants.CENTER ),
    pxTotalReqCPU                                   ( true  , 100 , SwingConstants.CENTER ),
    pxTrackerReportTimeCPU                          ( true  , 170 , SwingConstants.CENTER ),
    pxTrackerWatchTimeCPU                           ( true  , 162 , SwingConstants.CENTER ),
    pxTransientJavaAssembleCPU                      ( true  , 200 , SwingConstants.CENTER ),
    pxTransientJavaCompileCPU                       ( true  , 193 , SwingConstants.CENTER ),

    // -- BYTES --
    pxDBInputBytes                                  ( false , 108 , SwingConstants.RIGHT  ),
    pxDBInteractionInputBytes                       ( false , 193 , SwingConstants.RIGHT  ),
    pxDBOutputBytes                                 ( false , 116 , SwingConstants.RIGHT  ),
    pxInputBytes                                    ( false , 93  , SwingConstants.RIGHT  ),
    pxOutputBytes                                   ( false , 100 , SwingConstants.RIGHT  ),
    pxStaticContentBytes                            ( false , 154 , SwingConstants.RIGHT  ),
    pxStreamOutputBytes                             ( false , 147 , SwingConstants.RIGHT  ),

    // -- TIME --
    pxConnectInMapReqTime                           ( true  , 162 , SwingConstants.CENTER ),
    pxConnectOutMapReqTime                          ( true  , 170 , SwingConstants.CENTER ),
    pxTotalReqTime                                  ( true  , 108 , SwingConstants.CENTER ),

    // -- OTHERS --
    pxActivationSizeAverage                         ( false , 177 , SwingConstants.RIGHT  ),
    pxActivationSizeLast                            ( false , 154 , SwingConstants.RIGHT  ),
    pxActivationSizePeak                            ( false , 154 , SwingConstants.RIGHT  ),
    pxBytesAllocated                                ( false , 124 , SwingConstants.RIGHT  ),
    pxConnectRequestDataVolume                      ( false , 200 , SwingConstants.RIGHT  ),
    pxConnectResponseDataVolume                     ( false , 208 , SwingConstants.RIGHT  ),
    pxFrameTransactionMapSize                       ( false , 193 , SwingConstants.RIGHT  ),
    pxInteractions                                  ( false , 108 , SwingConstants.RIGHT  ),
    pxLookupListDBFetches                           ( false , 162 , SwingConstants.RIGHT  ),
    pxNewFUAInstances                               ( false , 131 , SwingConstants.RIGHT  ),
    pxOtherBrowseReturned                           ( false , 162 , SwingConstants.RIGHT  ),
    pxPassivationSizeAverage                        ( false , 185 , SwingConstants.RIGHT  ),
    pxPassivationSizeLast                           ( false , 162 , SwingConstants.RIGHT  ),
    pxPassivationSizePeak                           ( false , 162 , SwingConstants.RIGHT  ),
    pxRuleBrowseReturned                            ( false , 154 , SwingConstants.RIGHT  ),
    pxRulesExecuted                                 ( false , 116 , SwingConstants.RIGHT  ),
    pxRulesUsed                                     ( false , 85  , SwingConstants.RIGHT  ),
    pxServiceDataVolume                             ( false , 147 , SwingConstants.RIGHT  ),
    pxServiceNumFileRecords                         ( false , 177 , SwingConstants.RIGHT  );
    // CHECKSTYLE:ON
    // @formatter:on

    private final boolean doubleType;

    private final int prefColumnWidth;

    private final int horizontalAlignment;

    private PALStatisticName(boolean doubleType, int prefColumnWidth, int horizontalAlignment) {
        this.doubleType = doubleType;
        this.prefColumnWidth = prefColumnWidth;
        this.horizontalAlignment = horizontalAlignment;
    }

    public boolean isDoubleType() {
        return doubleType;
    }

    public int getPrefColumnWidth() {
        return prefColumnWidth;
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

}
