# [](https://github.com/phodal/shire/compare/v0.4.8...v) (2024-08-02)

## [Unreleased]

## [0.4.8](https://github.com/phodal/shire/compare/v0.4.7...v[0.4.8]) (2024-08-02)

### Bug Fixes

- **guard:** improve regex pattern validation and update UK phone pattern [#47](https://github.com/phodal/shire/issues/47) ([bcc7e16](https://github.com/phodal/shire/commit/bcc7e162ad328445c707d4df8a0041ac23d391ad))

### Features

- **core:** add GuardScanner interface and ScanResult data class [#47](https://github.com/phodal/shire/issues/47) ([b32b7d8](https://github.com/phodal/shire/commit/b32b7d8e53dd52129ea2a2280fe4682bba3591b8))
- **guard:** add matching and masking functions, remove Joni dependency [#47](https://github.com/phodal/shire/issues/47) ([6a12233](https://github.com/phodal/shire/commit/6a12233170625ed147694167b8c71690649f0266))
- **scanner:** refactor scanner classes and update documentation [#47](https://github.com/phodal/shire/issues/47) ([a223428](https://github.com/phodal/shire/commit/a223428131c757fdf1bf2cde8a0877895020d13c))
- **schema-provider:** add CustomAgentSchemaFileProvider to factory list [#47](https://github.com/phodal/shire/issues/47) ([c8999e5](https://github.com/phodal/shire/commit/c8999e59d6ea9fac58a59e2864680d89ec163253))
- **search:** add dimensions parameter to embedInternal function ([494a8b5](https://github.com/phodal/shire/commit/494a8b56f27d8e0f90338457e3276321fac724dc))
- **secrets-detection:** rename secretType to description and add LocalModelBasedScanner [#47](https://github.com/phodal/shire/issues/47) ([091bacd](https://github.com/phodal/shire/commit/091bacdaea7fe70542b1016840670998e2649d38))
- **secrets-guard:** implement regex-based secret detection [#47](https://github.com/phodal/shire/issues/47) ([1eb2517](https://github.com/phodal/shire/commit/1eb2517e69f2b46066d41e13fc07539eb021ddb2))
- **secrets:** implement regex patterns for PII detection ([a718ac7](https://github.com/phodal/shire/commit/a718ac7c248c7de043f7c2b7c641ed162b7bd7a4))
- **security:** implement BanKeywordsScanner and Replacer interface [#47](https://github.com/phodal/shire/issues/47) ([47f2b69](https://github.com/phodal/shire/commit/47f2b697b857c39d76d85f1338b7128288fb4b99))
- **shirelang:** implement redact function for data masking [#47](https://github.com/phodal/shire/issues/47) ([ed28585](https://github.com/phodal/shire/commit/ed285856f36e443687fc8bc75f3a70084d75723f))

## [0.4.7](https://github.com/phodal/shire/compare/v0.4.6...v[0.4.7]) (2024-07-30)

### Bug Fixes

- **core:** enhance InsertUtil to validate offset range ([022d8e2](https://github.com/phodal/shire/commit/022d8e249da9b5aaa7e7166cc69de2269ff05e34))

### Features

- **core:** add UpdateEditorTextProcessor ([1d8b566](https://github.com/phodal/shire/commit/1d8b566e2b2b0da7ba98f88fecb9ec8be89214c2))
- **docs:** update lifecycle documentation and examples ([9ca64fe](https://github.com/phodal/shire/commit/9ca64fee7870c5b9365541e4d4a67391758fd83f))
- **git-provider:** implement lookupGitData and ShireVcsCommit updates [#41](https://github.com/phodal/shire/issues/41) ([2d56eaf](https://github.com/phodal/shire/commit/2d56eafbdbd15c6824da891366529da3e6282d49))
- **llm:** enhance OpenAILikeProvider to include temperature setting ([cbb6f1d](https://github.com/phodal/shire/commit/cbb6f1d15041520c19f8f2ffde54b8ae7aa1bbeb))
- **provider:** add GitQLDataProvider and update ShireQLDataProvider interface [#41](https://github.com/phodal/shire/issues/41) ([5af617e](https://github.com/phodal/shire/commit/5af617e2ebbabd0908c0ef9d9c82eead4886a8f7))
- **settings:** add temperature setting and UI component ([264211f](https://github.com/phodal/shire/commit/264211f5937a0d42f10d90a8dfee92aae29035ac))
- **shirelang:** add VcsStatementProcessor for commit info handling [#41](https://github.com/phodal/shire/issues/41) ([c71fe57](https://github.com/phodal/shire/commit/c71fe57d5dcf73a37298139a2ac1dee34b4f5c91))

## [0.4.6](https://github.com/phodal/shire/compare/v0.4.5...v[0.4.6]) (2024-07-24)

### Bug Fixes

- **compiler:** handle exceptions when finding files ([e85eb79](https://github.com/phodal/shire/commit/e85eb79923442db4a519c62fa78242862cd16d76))

### Features

- **compiler:** add support for preserving last output in function execution ([73739f5](https://github.com/phodal/shire/commit/73739f5bf2bd1ab4c1fa2b71979690cfd53fd64f))
- **compiler:** enhance afterStreaming execution flow ([c2b7227](https://github.com/phodal/shire/commit/c2b7227abe7f129cbad9f553c2f759928709ba77))
- **core:** add MarkdownPsiContextVariableProvider ([5418957](https://github.com/phodal/shire/commit/5418957719f848ac0e28aac345c8b0a2e95c3dd4))
- **core:** allow null values in compiledVariables and refactor file execution ([20eed68](https://github.com/phodal/shire/commit/20eed683f0d265bcbbef2e710afe0784f320134b))
- **core:** enhance MarkdownPsiContextVariableProvider for file context ([33b97da](https://github.com/phodal/shire/commit/33b97da7fbb7db91ae34c397f8caf1165266f819))
- **core:** enhance MarkdownPsiContextVariableProvider for HTML conversion ([451e61d](https://github.com/phodal/shire/commit/451e61d73e370601437af95c12ce6364e8efa094))
- **custom:** add custom SSE handling ([5735a58](https://github.com/phodal/shire/commit/5735a589b4f7e45b3a750dfc736135b578eb0e08))
- **docs:** update response routing for Java and dynamic input ([0bd650d](https://github.com/phodal/shire/commit/0bd650dc75d39a4a966d8873551cd33b29c8ef11))
- **runner:** enhance Shire runner to handle last output ([4e2804b](https://github.com/phodal/shire/commit/4e2804b6501a7086c3e0437e1bff6e9fcaa6f309))
- **searching:** add similarity threshold to search function ([dd8cd73](https://github.com/phodal/shire/commit/dd8cd73c9c9466f14551650a685fb2d5f0538f03))
- **search:** normalize embeddings and update search methods ([ad907ec](https://github.com/phodal/shire/commit/ad907ec2b27c822d3c8b07467f550618133a6ebc))
- **Shirelang:** enhance HobbitHole and ShireRunFileAction for dynamic interaction ([18f3679](https://github.com/phodal/shire/commit/18f3679711dea6dcc1588a1d921dc9339ad03279))
- **shirelang:** improve file not found error logging ([043488e](https://github.com/phodal/shire/commit/043488e6c1a81066d2dda95bcc661d932af7fff1))
- **ShireRunner:** enhance error handling for detachProcess ([d221082](https://github.com/phodal/shire/commit/d22108282ac9e2fedba2442986708b70b19b35cd))
- **testing:** add new test case for afterStreamingOnly functionality ([a91be0d](https://github.com/phodal/shire/commit/a91be0d2532bc7540ac3bdfefbcdfc707513622c))

## [0.4.5](https://github.com/phodal/shire/compare/v0.4.3...v[0.4.5]) (2024-07-19)

### Features

- **core:** implement equals and hashCode for IndexEntry ([2543805](https://github.com/phodal/shire/commit/25438059ca7ee50a2c47fba29a7e0cc8ef8ed677))
- **search:** add interface for similarity algorithms ([27c65c1](https://github.com/phodal/shire/commit/27c65c1c0dd85c7c91ddcfd72ba90470c03136f8))
- **search:** implement BM25 similarity algorithm and refactor SimilarChunkSearcher ([ef32475](https://github.com/phodal/shire/commit/ef3247552446f1218bd6a0ea30857cb952160933))

## [0.4.3](https://github.com/phodal/shire/compare/v0.4.2...v[0.4.3]) (2024-07-14)

### Bug Fixes

- **actions:** handle null hole in context menu actions ([4a04f35](https://github.com/phodal/shire/commit/4a04f356b16689514bdc4af10df30b8d136b8b6a))

### Features

- **compiler:** add Tee class for writing to files [#36](https://github.com/phodal/shire/issues/36) ([153cc93](https://github.com/phodal/shire/commit/153cc93097f162e96b2f37aa1011f3caf6178886))
- **interaction:** add PasteBoard interaction type ([33afb37](https://github.com/phodal/shire/commit/33afb37775288360865cb3e47b6a943bee1895bd))
- **middleware:** add append functionality and AppendProcessor [#36](https://github.com/phodal/shire/issues/36) ([7413368](https://github.com/phodal/shire/commit/74133686a19869e25ff84a5c0392c081c9df787d))
- **middleware:** expose and update compiledVariables across components [#36](https://github.com/phodal/shire/issues/36) ([c83ffbe](https://github.com/phodal/shire/commit/c83ffbe27659d96139ee3b7165aa6982495c1187))
- **provider:** add method support for JavaPsiQLInterpreter ([ad83803](https://github.com/phodal/shire/commit/ad838038fefac63ad64c4e24351e34db50319f89))
- **provider:** add PsiQLMethodCallInterpreter interface ([c9b6606](https://github.com/phodal/shire/commit/c9b6606d127d0a4e4f6df2d1c0229569272f5e25))
- **shirelang:** extend pipelineArg syntax and add contentTee test [#36](https://github.com/phodal/shire/issues/36) ([42031b7](https://github.com/phodal/shire/commit/42031b7065886b9e399b07497e5157c2d2cd5652))

## [0.4.2](https://github.com/phodal/shire/compare/v0.4.1...v[0.4.2]) (2024-07-09)

### Bug Fixes

- **git:** handle null data context in GitToolchainVariableProvider ([69c48cd](https://github.com/phodal/shire/commit/69c48cd74e444cebdbeadae31bcf84b71c11a0ed))

### Features

- **build:** add kotlinx-coroutines-core dependency ([836332b](https://github.com/phodal/shire/commit/836332bfdca2481f4762a331b7d82b207a4f374b))
- **run:** add console message before running configuration ([a27e899](https://github.com/phodal/shire/commit/a27e8993cd2090293d42764fb9215a7955853c84))
- **search:** add caching support to semantic search ([7ef7287](https://github.com/phodal/shire/commit/7ef7287ecd15b27726d456587942204ccb184005))

## [0.4.1](https://github.com/phodal/shire/compare/v0.3.0...v[0.4.1]) (2024-07-07)

### Bug Fixes

- **compiler:** handle null defaultTask in TaskRoutes.kt ([10cfc67](https://github.com/phodal/shire/commit/10cfc67464c249822a1abdc400bbadd32726537b))
- **completion:** update HobbitHoleValueCompletion to display action location with descriptions. ([71153b4](https://github.com/phodal/shire/commit/71153b4a8aed7d8c617b1ec37cd39762a04ca1cf))
- **config:** handle nullable Flow<String> in EditorInteractionProvider ([6086166](https://github.com/phodal/shire/commit/6086166d6e95bf7c1d24e13fba5e6587ed74d349))
- **core:** ensure thread safety in BaseCodeGenTask.kt ([4479e8a](https://github.com/phodal/shire/commit/4479e8aef4356f49ac6bb2ee7c5674415e3c6359))
- **run:** handle line markers for leaf elements only ([62f09f6](https://github.com/phodal/shire/commit/62f09f6399e03b5e14500b268341e99ad5ab80f1))
- **runner:** detach process handler in ShireRunner ([041c356](https://github.com/phodal/shire/commit/041c35682330af2257c80b8bb63377b0f5b72317))
- **shirelang:** check for null before adding action to toolsMenu ([289f578](https://github.com/phodal/shire/commit/289f57876eecea204acdabde87dec7016b5d4857))
- **shirelang:** update line marker provider to support front matter entries ([2e843a9](https://github.com/phodal/shire/commit/2e843a913b19fb201190212f5d35244064495274))

### Features

- **actions:** add method to set keymap shortcut ([f99686a](https://github.com/phodal/shire/commit/f99686a6017f9a50a506aa31a77f6902ff96adae))
- **code-completion:** refactor code completion task and add InsertUtil [#29](https://github.com/phodal/shire/issues/29) ([b02987e](https://github.com/phodal/shire/commit/b02987edb180f8dc4db36a607651d512fc3b6d1e))
- **compiler:** add CaseMatch functionality in PatternActionFunc [#29](https://github.com/phodal/shire/issues/29) ([0521198](https://github.com/phodal/shire/commit/05211984004d3e0b96e77c76a6a252e168eafd87))
- **compiler:** add save file functionality ([c6bbde6](https://github.com/phodal/shire/commit/c6bbde6e87f9aa065e7daa9832f6bacec1197878))
- **compiler:** add support for WHEN condition and VARIABLES ([593abd5](https://github.com/phodal/shire/commit/593abd56c897d527891ae0e365f177aad0809b18))
- **completion:** add PostProcessor completion provider ([4968586](https://github.com/phodal/shire/commit/49685869b416679d0c050d06d2bfa523e3266c6d))
- **completion:** add PSI context variables to VariableCompletionProvider [#29](https://github.com/phodal/shire/issues/29) ([564b0fe](https://github.com/phodal/shire/commit/564b0fe36aa318bb852b08a5e000edc05d315be5))
- **completion:** add QueryStatementCompletion provider ([696a306](https://github.com/phodal/shire/commit/696a3069f8e4302f94fdfed4c5b0d87e56511580))
- **core:** add code completion task and related changes [#29](https://github.com/phodal/shire/issues/29) ([34eb6fe](https://github.com/phodal/shire/commit/34eb6fec9f5d70b99068870382f7fe8f1cc7154c))
- **core:** add GitActionLocationEditor for commit menu ([48db3c4](https://github.com/phodal/shire/commit/48db3c4e1325ab93201fcde518cd4924cc58f2f8))
- **core:** add postExecute callback to code execution tasks [#29](https://github.com/phodal/shire/issues/29) ([af6567b](https://github.com/phodal/shire/commit/af6567bfdf418643d44af590ce1a266b422b8a92))
- **core:** add postExecute invocation and update interactionType in ShireDefaultRunner [#29](https://github.com/phodal/shire/issues/29) ([8bc4b8b](https://github.com/phodal/shire/commit/8bc4b8bf2b9ee1397286b1769db504b953e72c29))
- **core:** add reflection support for ToolchainVariable ([72ec463](https://github.com/phodal/shire/commit/72ec463441f50c0a65adc174c65e8a7fcdc86700))
- **docs:** add agent examples and documentation provider ([8d7aafe](https://github.com/phodal/shire/commit/8d7aafeb9107503fa3797700a214db19b37939de))
- **docs:** add examples for code comments, refactoring, CLI copilot, and commit message generation ([1e07f68](https://github.com/phodal/shire/commit/1e07f689fc50d0eaa4eff7c72339765d6b646dff))
- **EditorInteractionProvider:** enhance task creation and error handling [#29](https://github.com/phodal/shire/issues/29) ([e0f34a3](https://github.com/phodal/shire/commit/e0f34a381cc19847330881966d02956f891786f8))
- **git:** add commit message UI retrieval improvement in ShireVcsSingleAction ([a8f11de](https://github.com/phodal/shire/commit/a8f11de61dcc51fa55a128f05140a8e83223b73d))
- **input:** add custom input box action ([8ec40a4](https://github.com/phodal/shire/commit/8ec40a41517b17f5644268a9eb2ea885233377c6))
- **interaction:** add support for running code in Run panel ([7c4e8d5](https://github.com/phodal/shire/commit/7c4e8d5f4f798e73b01fa91a473afb66c3e1ec5c))
- **interaction:** improve code completion and generation tasks [#29](https://github.com/phodal/shire/issues/29) ([957e75d](https://github.com/phodal/shire/commit/957e75dbef245538472313595882c872093c1309))
- **interaction:** refactor code generation tasks and add BaseCodeGenTask [#29](https://github.com/phodal/shire/issues/29) ([0f84b3b](https://github.com/phodal/shire/commit/0f84b3bd80af33b07c5831ad5441b9df1ee9a7a8))
- **java:** add class structure representation and data builder [#29](https://github.com/phodal/shire/issues/29) ([90f2364](https://github.com/phodal/shire/commit/90f2364ffbbb45f73a6e09e801461080d8a9010a))
- **java:** add method caller and called method lookup [#29](https://github.com/phodal/shire/issues/29) ([6b46c00](https://github.com/phodal/shire/commit/6b46c001b5f0a209b8cfd65418205c5206698852))
- **java:** add methods to retrieve containing class and method ([b4c26ea](https://github.com/phodal/shire/commit/b4c26eaf868a1dfb9845d377b330b933d97eda61))
- **keyboard:** add support for setting keymap shortcuts ([4eda080](https://github.com/phodal/shire/commit/4eda0800f6b168c315b1ce72231a66508ef625ef))
- **lints:** add duplicate agent inspection ([7947d3c](https://github.com/phodal/shire/commit/7947d3cd92329d9e81a241682646dfdb566e0002))
- **logging:** improve error handling and logging in ShireActionStartupActivity ([e661e16](https://github.com/phodal/shire/commit/e661e161f51a7837cbdb88387bbc3b7533eb306e))
- **middleware:** add InsertNewlineProcessor ([73cebef](https://github.com/phodal/shire/commit/73cebef93bff5e71558938e8d6153f906276a895))
- **middleware:** add ParseCommentProcessor ([aafb938](https://github.com/phodal/shire/commit/aafb9386c5e9e55a54577c531cd770c83a557f5f))
- **provider:** add terminal location executor ([99c93a0](https://github.com/phodal/shire/commit/99c93a0da396940ddd1f2fe1c186474d609fbcce))
- **runner:** add support for user input in Shire configuration ([6ffebd6](https://github.com/phodal/shire/commit/6ffebd6a2fd0ab5004dde65526b2eb11de395dd1))
- **runner:** refactor ShireRunner to improve terminal task execution and error handling ([b236ad2](https://github.com/phodal/shire/commit/b236ad2f5c7b238c26e7e021d17991aaf312bc0f))
- **shirelang:** add icon support and improve line marker provider [#29](https://github.com/phodal/shire/issues/29) ([f12c199](https://github.com/phodal/shire/commit/f12c19900f307ff84e057b304564e0b5d37e5454))
- **shirelang:** add ShirePsiExprLineMarkerProvider for line marker support [#29](https://github.com/phodal/shire/issues/29) ([1a5c3ca](https://github.com/phodal/shire/commit/1a5c3ca56752179586baf390c85a4f21f2c4bd07))
- **shirelang:** refactor and improve pattern action processing [#29](https://github.com/phodal/shire/issues/29) ([57689a2](https://github.com/phodal/shire/commit/57689a2ae0e8c803bbeb6e646e95f39ec095a8e9))
- **shirelang:** update line marker provider for ShirePsiExpr ([6448aa1](https://github.com/phodal/shire/commit/6448aa154ccc9a6e02213cf664dc7d6aa84aa05d))
- **shirelang:** update line marker provider to support front matter entries ([1d4bb4b](https://github.com/phodal/shire/commit/1d4bb4b4c9403b88ede31b2ca38b92e1ce4d8fef))
- **terminal:** add input box popup for terminal action ([03dce26](https://github.com/phodal/shire/commit/03dce2625011eea61cd3b2b5e0860527a904229f))
- **terminal:** add shell command suggestion action ([958340c](https://github.com/phodal/shire/commit/958340c89a7dcc8e6e1eef190d31f409356be83c))
- **terminal:** add ShireTerminalAction for custom assistants ([01023de](https://github.com/phodal/shire/commit/01023de953d69115760d0b6c29a085f344a2f067))
- **terminal:** add TerminalToolchainVariableProvider ([8b13dc3](https://github.com/phodal/shire/commit/8b13dc39d88636f34196e7807c700d94f957feb3))
- **variable:** add BuiltinVariable and resolver ([f88af49](https://github.com/phodal/shire/commit/f88af49fe0bc197eb9d1443c0addafb1d7157667))
- **variable:** add SystemInfoVariable and resolver ([c1a54eb](https://github.com/phodal/shire/commit/c1a54eb4cdc1f06cbb0b47d7ce58e639d56404bd))
- **VariableCompletionProvider:** add icon to variable lookup elements [#29](https://github.com/phodal/shire/issues/29) ([7ae12c5](https://github.com/phodal/shire/commit/7ae12c5f071c1ec494f7410f482a4375a1eba01b))
- **variables:** add code smell detection and test data generation [#29](https://github.com/phodal/shire/issues/29) ([3763184](https://github.com/phodal/shire/commit/3763184da20ade0d3df8a9916730855438502f41))
- **variables:** add support for similar code search ([5fe7f8f](https://github.com/phodal/shire/commit/5fe7f8f2db96727312d56101e77abff9726e9138))
- **vcs): add Shirefeat VCS single(vcs:** action ([e982cec](https://github.com/phodal/shire/commit/e982cecd524b786fd0c30d12770587286082e3f4))

## [0.0.8](https://github.com/phodal/shire/compare/v0.0.7...v[0.0.8]) (2024-07-01)

### Bug Fixes

- **compiler:** wrap parsing operations in read actions ([97c8d15](https://github.com/phodal/shire/commit/97c8d156b78971f787bcfd202eb6f6aa3f030a06))
- **compiler:** wrap parsing operations in read actions ([76e1700](https://github.com/phodal/shire/commit/76e1700b7b213d73de07f53053df28c96bba7905))
- **completion:** fix code fence insertion in completion ([7beb240](https://github.com/phodal/shire/commit/7beb240a2eaafd997e85d82e52330ea5f5fb3cf2))
- **shirelang:** refine action body parsing in FrontmatterParser kt file ([32776cd](https://github.com/phodal/shire/commit/32776cdf389710c941010a3d117bd5076202d1b1))
- **shirelang:** update when condition syntax and test evaluation ([2ee436c](https://github.com/phodal/shire/commit/2ee436c73362d06ecb7dd1858c42d094f0205a34))

### Features

- **actions:** refactor action groups and context menu action ([900d613](https://github.com/phodal/shire/commit/900d613900ed6dd9785b8ea3a1b082065db18f54))
- **chat:** add ChatRole enum and FileGenerateTask for file output ([9bf98d5](https://github.com/phodal/shire/commit/9bf98d590d0c9a5e6726969009809fdefe02d4a8))
- **compiler:** add 'afterStreaming' feature and enhance pattern action processing [#24](https://github.com/phodal/shire/issues/24) ([bb21198](https://github.com/phodal/shire/commit/bb2119819f27bdc966dc0949f4b79681989bccaf))
- **compiler:** add FunctionStatementProcessor and refactor related classes [#24](https://github.com/phodal/shire/issues/24) ([1179c5e](https://github.com/phodal/shire/commit/1179c5e2089a1bd821e17dcd8d37a54c3a3977ca))
- **compiler:** add jsonpath support and modify error condition [#24](https://github.com/phodal/shire/issues/24) ([1913e4f](https://github.com/phodal/shire/commit/1913e4f994c208d624dbed7f70caff4e888e297e))
- **compiler:** enhance function statement processing and add new pattern actions [#24](https://github.com/phodal/shire/issues/24) ([054fec5](https://github.com/phodal/shire/commit/054fec5ae136a0507a715bd5c222351616e81c73))
- **compiler:** enhance statement processing in FunctionStatementProcessor [#24](https://github.com/phodal/shire/issues/24) ([56a551c](https://github.com/phodal/shire/commit/56a551c2b40ec86c59ee4ca2226040f5e0ef9a57))
- **compiler:** refactor function execution and improve logging [#24](https://github.com/phodal/shire/issues/24) ([8944045](https://github.com/phodal/shire/commit/8944045043e5b6b4b00f18e4c49c27f97aade484))
- **compiler:** refactor method invocation in FunctionStatementProcessor ([5b4911b](https://github.com/phodal/shire/commit/5b4911b38c8a9f1808a6028e8ea596c86c21d041))
- **compiler:** update function execution and case matching logic [#24](https://github.com/phodal/shire/issues/24) ([ae44272](https://github.com/phodal/shire/commit/ae44272ddab12bedfa89cd1efd6cc7dc53253809))
- **core:** enhance code parsing and saving [#24](https://github.com/phodal/shire/issues/24) ([0e95539](https://github.com/phodal/shire/commit/0e95539119bddbf5c1ffd5f95179fa28f6da3213))
- **core:** modify execute method to return string [#27](https://github.com/phodal/shire/issues/27) ([aeaf5d4](https://github.com/phodal/shire/commit/aeaf5d488b98da2db5adccc1d8159f6f8912dff8))
- **core:** refactor LlmProvider and related classes to shirecore package [#27](https://github.com/phodal/shire/issues/27) ([92e4026](https://github.com/phodal/shire/commit/92e4026c5407c9fa59e1854e24f7b5712797dcb3))
- **core:** update InteractionType and improve coroutine handling ([fffda5c](https://github.com/phodal/shire/commit/fffda5cc122eb48f60dc1063648e9aa81f22e7ec))
- **custom:** add custom SSE processor and JSON response callback [#10](https://github.com/phodal/shire/issues/10) ([6f75068](https://github.com/phodal/shire/commit/6f750687997d7660c73be3aa38781871da0f9a27))
- **docs:** add custom AI agent quickstart guide [#10](https://github.com/phodal/shire/issues/10) ([03f777b](https://github.com/phodal/shire/commit/03f777bf3437316259e9b932c0c01aada1592881))
- **docs:** add IDE note and update GitToolchainVariableProvider [#27](https://github.com/phodal/shire/issues/27) ([d59fcb8](https://github.com/phodal/shire/commit/d59fcb804185a9eb94a71b297b741ecf7e18f7e2))
- **editor:** add smart code insertion feature [#24](https://github.com/phodal/shire/issues/24) ([788051f](https://github.com/phodal/shire/commit/788051f45f39712844536c4b8460c440a403b799))
- **folding:** add support for query statements and block comments ([32e1da8](https://github.com/phodal/shire/commit/32e1da8e9287509d7e561aeda12fa081eaa2ee21))
- **grammar:** add support for QUOTE_STRING in agentId ([3fdf55c](https://github.com/phodal/shire/commit/3fdf55c59a306e3884a8fd37aecee94c610b0a68))
- **InteractionType:** add new interaction type and change defaults ([b944f93](https://github.com/phodal/shire/commit/b944f935b4a891297d03933fbf7517e0eced93f3))
- **java:** add smart insert method in JavaCodeModifier ([42d8326](https://github.com/phodal/shire/commit/42d8326c95c5df03d34beb11b8b9a08374fc6884))
- **lexer:** add brace level tracking and state transitions [#16](https://github.com/phodal/shire/issues/16) ([882444a](https://github.com/phodal/shire/commit/882444a62f0053e44705a058632a4c73abd784ef))
- **lexer:** add support for multiple front matter variables [#16](https://github.com/phodal/shire/issues/16) ([9eb9f8d](https://github.com/phodal/shire/commit/9eb9f8d6ed8761f33f3f3736e51b285667004867))
- **middleware:** Add console parameter to PostProcessor execute methods [#24](https://github.com/phodal/shire/issues/24) ([ebcbd94](https://github.com/phodal/shire/commit/ebcbd944182804dfa44050f46ef38e42f658eb96))
- **middleware:** add FormatCode functionality ([6905f4f](https://github.com/phodal/shire/commit/6905f4f25dea2a7757811a8b14076f5afd0e045c))
- **middleware:** add OpenFileProcessor to handle file opening [#24](https://github.com/phodal/shire/issues/24) ([99a2ec2](https://github.com/phodal/shire/commit/99a2ec2efb2f04194c927ccab4ef767466f9867c))
- **middleware:** add RunCodeProcessor and ExtensionPoint for file execution [#24](https://github.com/phodal/shire/issues/24) ([0b9f065](https://github.com/phodal/shire/commit/0b9f065f7b4925158429f43152429e38b38dcd21))
- **middleware:** enhance code execution, file saving and verification [#24](https://github.com/phodal/shire/issues/24) ([ed7ca3b](https://github.com/phodal/shire/commit/ed7ca3baaa7af894af532499d33c86b704af4d9f))
- **parser:** add parentheses detection in method calls [#16](https://github.com/phodal/shire/issues/16) ([29abe86](https://github.com/phodal/shire/commit/29abe86737527e152a686bcf32c89e74849aa574))
- **plugin:** add Python support to Shirelang plugin [#24](https://github.com/phodal/shire/issues/24) ([7da1a1f](https://github.com/phodal/shire/commit/7da1a1ff6e798613740924fefb21cbb58cc6e92e))
- **QueryStatementProcessor:** enhance error logging for method or field not found [#16](https://github.com/phodal/shire/issues/16) ([9dd696f](https://github.com/phodal/shire/commit/9dd696fac416c077712e5a998242af664e700436))
- **run-code:** add CLI execution support for Python, JavaScript, and Ruby files [#24](https://github.com/phodal/shire/issues/24) ([0619ecb](https://github.com/phodal/shire/commit/0619ecb511d8976a5b4535df1f6debbf035388b1))
- **runFile:** wrap file search in runReadAction for thread safety ([d8324c3](https://github.com/phodal/shire/commit/d8324c350f2e7580d613cde6e90e2bada8e1bc7a))
- **runner:** refactor interaction handling in IDE locations [#27](https://github.com/phodal/shire/issues/27) ([e51dc98](https://github.com/phodal/shire/commit/e51dc98a938b291b67dc68fcd9e47b05ba25486b))
- **runners:** add HobbitHole to ShireRunner classes [#24](https://github.com/phodal/shire/issues/24) ([9e55bd1](https://github.com/phodal/shire/commit/9e55bd1d97de1c5ce2e9151a65c05cb7950ab29f))
- **schema:** add Shire Custom Agent schema provider factory [#16](https://github.com/phodal/shire/issues/16) ([dc03d0c](https://github.com/phodal/shire/commit/dc03d0c08d50c146ecd9539730db84c497f734a8))
- **shire-core:** implement EditorInteractionProvider and add Shire Toolchain Variable doc [#27](https://github.com/phodal/shire/issues/27) ([aff5380](https://github.com/phodal/shire/commit/aff5380c05edf560a202de52947812b1d58994fa))
- **shirelang:** add default condition and new test for afterStreaming [#24](https://github.com/phodal/shire/issues/24) ([e5a28cb](https://github.com/phodal/shire/commit/e5a28cbe8e1a5036dd24080883b8c3ce71590224))
- **shirelang:** add file execution support and improve condition handling ([d37e306](https://github.com/phodal/shire/commit/d37e3060f47d5b8e3ff290d18ac2cc59d7beebd8)), closes [#24](https://github.com/phodal/shire/issues/24)
- **shirelang:** add new lifecycle keywords and update grammar [#24](https://github.com/phodal/shire/issues/24) ([c957282](https://github.com/phodal/shire/commit/c9572825a23a9c2c3ded709427784d3f736f7532))
- **shirelang:** add new lifecycle keywords to syntax highlighter ([3e43d38](https://github.com/phodal/shire/commit/3e43d38282bc35e46ff6785f03f0371f8ee9f75b))
- **shirelang:** add support for finish flags in output control flow [#24](https://github.com/phodal/shire/issues/24) ([99f1023](https://github.com/phodal/shire/commit/99f10230c6c856d6fe429c5c8c8b996d835525ee))
- **shirelang:** enhance pattern action processing and test handling [#24](https://github.com/phodal/shire/issues/24) ([cb59bfb](https://github.com/phodal/shire/commit/cb59bfb8233c027ace91680c750ffe38a63a778b))
- **TaskRoutes:** make defaultTask optional and refactor execution logic [#24](https://github.com/phodal/shire/issues/24) ([5787cca](https://github.com/phodal/shire/commit/5787cca2ad6fad9e00b59e5108e701a267d5f5e0))
- **testing:** add success condition and improve function execution [#24](https://github.com/phodal/shire/issues/24) ([6eadfda](https://github.com/phodal/shire/commit/6eadfdaae2deb177158c134f4b72ddee51397f69))
- **variable resolver:** add project context to resolveAll method [#27](https://github.com/phodal/shire/issues/27) ([b5f05f9](https://github.com/phodal/shire/commit/b5f05f940c3619db1c5ee38e9ca235ce4fdc6ffb))
- **variable-resolver:** add support for toolchain variables [#27](https://github.com/phodal/shire/issues/27) ([fdbb4c9](https://github.com/phodal/shire/commit/fdbb4c91b01f9ef78b0a0752d545c70f801fab02))
- **variables:** add ToolsetVariable and ToolsetVariableProvider [#27](https://github.com/phodal/shire/issues/27) ([c1da8a5](https://github.com/phodal/shire/commit/c1da8a5d3ac7be9c8452f2f9c34bd5aa49a5dd86))
- **vcs:** add dynamic actions to VCS action group [#24](https://github.com/phodal/shire/issues/24) ([9018da5](https://github.com/phodal/shire/commit/9018da5056323b148a9c5d797c9f994efb1efd93))
- **vcs:** add ShireVcsActionGroup for dynamic actions ([902bc2a](https://github.com/phodal/shire/commit/902bc2ab24df996bfc1fd7bd47eb5204b5dc916c))
- wrap code blocks with appropriate application run actions [#24](https://github.com/phodal/shire/issues/24) ([55f7495](https://github.com/phodal/shire/commit/55f7495db0c4a23adb3e736545a61dc2d6555ee4))

## [0.0.7](https://github.com/phodal/shire/compare/v0.0.6...v[0.0.7]) (2024-06-24)

### Bug Fixes

- **grammar:** update frontMatterArray syntax in ShireParser.bnf [#16](https://github.com/phodal/shire/issues/16) ([428033c](https://github.com/phodal/shire/commit/428033cb0b365e23e2e6fd77981ac38208c732ad))
- **pattern-searcher:** handle invalid regex and refactor code [#18](https://github.com/phodal/shire/issues/18) ([b00cd54](https://github.com/phodal/shire/commit/b00cd54bc1d9eb82a2dd259e2617f89b33ff6831))

### Features

- **codemodel:** add FileStructure and VariableStructure classes [#14](https://github.com/phodal/shire/issues/14) ([322e897](https://github.com/phodal/shire/commit/322e89770f2ef3b93804f514705c0d5f220a4111))
- **codemodel:** add MethodStructureProvider and related modifications [#14](https://github.com/phodal/shire/issues/14) ([29eedb9](https://github.com/phodal/shire/commit/29eedb9f5c00bcdcb78554f2c0c4e42d74dd78e2))
- **codemodel:** add VariableStructureProvider and FileStructureProvider, refactor MethodStructureProvider [#14](https://github.com/phodal/shire/issues/14) ([4be010b](https://github.com/phodal/shire/commit/4be010b8ca205aba73159d8436b1ab0d9049daa8))
- **codemodel:** enhance ClassStructure and MethodStructure formatting [#14](https://github.com/phodal/shire/issues/14) ([07488b0](https://github.com/phodal/shire/commit/07488b029ba653957738d226eb464f1270b64ebc))
- **codemodel:** update FileStructure and add DirectoryStructure [#14](https://github.com/phodal/shire/issues/14) ([076368e](https://github.com/phodal/shire/commit/076368ed87806d32bbfe4453ccdca613ad9a8f22))
- **compiler:** add error handling for function arguments ([376691f](https://github.com/phodal/shire/commit/376691f8ad251658282c69e62525cdcfc8ccee3d))
- **compiler:** add execution logic for variable pattern functions [#16](https://github.com/phodal/shire/issues/16) ([3542461](https://github.com/phodal/shire/commit/354246175027b6ff0cd8df14c1af0e3cd53edb0f))
- **compiler:** add operator handling in QueryStatementProcessor [#16](https://github.com/phodal/shire/issues/16) ([fc9f89d](https://github.com/phodal/shire/commit/fc9f89d6664e475f85227724eebf2496d8f26c16))
- **compiler:** add PatternSearcher for file matching by regex [#18](https://github.com/phodal/shire/issues/18) ([d129e40](https://github.com/phodal/shire/commit/d129e406478cfaba891b8ea57a608149da7796f9))
- **compiler:** add query statement support in pattern action [#16](https://github.com/phodal/shire/issues/16) ([12fd4fd](https://github.com/phodal/shire/commit/12fd4fda5671881128059ccdb844f12fa210f8dc))
- **compiler:** add regex support and array handling in PatternActionFunc [#18](https://github.com/phodal/shire/issues/18) ([aebf478](https://github.com/phodal/shire/commit/aebf478ba9b04a9ac4240809b7699a6edd6ddc73))
- **compiler:** add support for query statements in Shire language [#16](https://github.com/phodal/shire/issues/16) ([824c9f0](https://github.com/phodal/shire/commit/824c9f009ca0e428779703d30daf65ed7db65137))
- **compiler:** add Value class and evaluate function in ShireExpression [#16](https://github.com/phodal/shire/issues/16) ([8b04548](https://github.com/phodal/shire/commit/8b0454857ef01d31e669f69488d7ba277005413c))
- **compiler:** enhance QueryStatementProcessor functionality [#16](https://github.com/phodal/shire/issues/16) ([ddc2ff5](https://github.com/phodal/shire/commit/ddc2ff51e6b38d17d4de7db9a31de2366537e4b3))
- **compiler:** handle null arguments and improve error handling [#16](https://github.com/phodal/shire/issues/16) ([59502be](https://github.com/phodal/shire/commit/59502be32bcd45294fc884be19728eecdbe2327a))
- **compiler:** improve pattern action execution and testing [#18](https://github.com/phodal/shire/issues/18) ([4cada42](https://github.com/phodal/shire/commit/4cada4234799def70e7fc64534d8a7b50bd6acc2))
- **compiler:** improve pattern handling and file loading in ShireCompiler [#18](https://github.com/phodal/shire/issues/18) ([4f3062e](https://github.com/phodal/shire/commit/4f3062e4e3b4ca5e95db03a5c48c9f4f12ed424b))
- **compiler:** refactor built-in methods to enum class [#18](https://github.com/phodal/shire/issues/18) ([07e0475](https://github.com/phodal/shire/commit/07e0475851e59c0778254d6522666705ba78f58a))
- **compiler:** refactor pattern action classes and move to ast package [#18](https://github.com/phodal/shire/issues/18) ([35a5515](https://github.com/phodal/shire/commit/35a55153df29b82c715d010237ef0bf6ab916349))
- **compiler:** refactor query statement parsing and improve documentation [#16](https://github.com/phodal/shire/issues/16) ([6e385d5](https://github.com/phodal/shire/commit/6e385d58b2055f8d8faa0c37a308bd00d8f53f5d))
- **compiler:** refactor template compilation and variable resolution ([93d7536](https://github.com/phodal/shire/commit/93d75369b19e13938ab604e3be7d08f42c31db90)), closes [#18](https://github.com/phodal/shire/issues/18)
- **compiler:** refactor VariableStatement to VariableElement and add new PatternActionFunc subclasses [#16](https://github.com/phodal/shire/issues/16) ([6c9c9f3](https://github.com/phodal/shire/commit/6c9c9f3eddc5f33130fb6401250ff54d00571495))
- **console:** add console output for Shirelang execution and error handling [#18](https://github.com/phodal/shire/issues/18) ([b9e4bb1](https://github.com/phodal/shire/commit/b9e4bb19e8b04b828513ae5066ee4169e73b8b2d))
- **core:** add comment and refactor code in CustomAgent, ClassStructure, ShireExpression ([d5c0573](https://github.com/phodal/shire/commit/d5c05735dc7680c9cfc1060986f29c683a7da425))
- **docs, core, languages:** add design samples and method context variables ([bf99f08](https://github.com/phodal/shire/commit/bf99f086f14796d42a82b92d801a8968eac958e7))
- **FrontMatterType:** add QUERY_STATEMENT subclass [#16](https://github.com/phodal/shire/issues/16) ([a7ccc1a](https://github.com/phodal/shire/commit/a7ccc1a2629762fb11e0507bdc5188790610c997))
- **java-toolchain:** add Maven support and refactor tech stack detection [#18](https://github.com/phodal/shire/issues/18) ([9c14272](https://github.com/phodal/shire/commit/9c1427233e41d329add38933c399eb1110b80385))
- **java-toolchain:** refactor Maven build tool functionality into separate class [#18](https://github.com/phodal/shire/issues/18) ([39f4783](https://github.com/phodal/shire/commit/39f47837a79b159c1db04c2c7badfd1dd2863264))
- **java-toolchain:** replace JavaTasksUtil with GradleTasksUtil [#18](https://github.com/phodal/shire/issues/18) ([83c5e00](https://github.com/phodal/shire/commit/83c5e00f642226c5a0fb2b16d9e9d303bc666afe))
- **java:** enhance RelatedClassesProvider to support PsiClass [#14](https://github.com/phodal/shire/issues/14) ([f39aa4e](https://github.com/phodal/shire/commit/f39aa4e59f8472e7989c240c94ec061f5fdb36be))
- **java:** refactor method name and add JavaCodeModifier [#14](https://github.com/phodal/shire/issues/14) ([319cd74](https://github.com/phodal/shire/commit/319cd740b96e5217d27f92495dc7d9023b1f9410))
- **parser:** add 'and' operator and improve comment handling [#16](https://github.com/phodal/shire/issues/16) ([153c5a4](https://github.com/phodal/shire/commit/153c5a447fbe6ec575f04a5951fb917ce9a3a083))
- **parser:** add support for comments in ShireParserDefinition [#16](https://github.com/phodal/shire/issues/16) ([dbdf410](https://github.com/phodal/shire/commit/dbdf4108077c40d6440b71dd1710acf8e5858ba7))
- **parser:** remove whitespace support in query and from clauses [#16](https://github.com/phodal/shire/issues/16) ([9ac9d82](https://github.com/phodal/shire/commit/9ac9d8210727d584a77a72ff2806ce3730aa2c84))
- **parser:** update grammar and lexer for query expressions [#16](https://github.com/phodal/shire/issues/16) ([7fd6746](https://github.com/phodal/shire/commit/7fd67469b351e12af06d32770ecf0a43b64e00ec))
- **parsing:** update grammar and lexer for query expressions [#16](https://github.com/phodal/shire/issues/16) ([383e1ac](https://github.com/phodal/shire/commit/383e1ac48807f9b7befbfaa0f47344eb953e84d0))
- **pattern-action:** add array handling in Grep function [#18](https://github.com/phodal/shire/issues/18) ([9a76f1c](https://github.com/phodal/shire/commit/9a76f1cd3f5237534bae63d69937e7d1444e1f40))
- **plugin:** add Shire plugin configuration and enhance symbol lookup [#16](https://github.com/phodal/shire/issues/16) ([d25e77b](https://github.com/phodal/shire/commit/d25e77bac3f17cd0a9bb0964929b906dbe6ceb4b))
- **QueryStatementProcessor:** implement Expression and String cases ([915dd81](https://github.com/phodal/shire/commit/915dd81ddb7c710263d8e0f38f82514239d1007e))
- **QueryStatementProcessor:** implement operator and type evaluation [#16](https://github.com/phodal/shire/issues/16) ([4369408](https://github.com/phodal/shire/commit/4369408a74328b8a46418122f9e5a4e944f71e8e))
- remove saql project from build ([53d4d1f](https://github.com/phodal/shire/commit/53d4d1f919cf2219a9fbba6c053cc55b6af39047))
- **runner:** add system info variable resolution [#16](https://github.com/phodal/shire/issues/16) ([9f5d86b](https://github.com/phodal/shire/commit/9f5d86b91c29da377bcfe9eda39f402f84fa581d))
- **runner:** enhance data fetching methods in SystemInfoVariable ([411ebf4](https://github.com/phodal/shire/commit/411ebf4840152624f15a6800478bcf5715fdbf74))
- **saql:** add lexer and parser for Shire SQL [#16](https://github.com/phodal/shire/issues/16) ([a4479c3](https://github.com/phodal/shire/commit/a4479c32b9c72a6f6c57e8be490d825e8bf7fcfb))
- **saql:** add saql project and related files [#16](https://github.com/phodal/shire/issues/16) ([572694e](https://github.com/phodal/shire/commit/572694e1a129f2786a8e1e0fd15ff5584f2cea43))
- **saql:** enable getSqlTable methods in SAQLParser ([16d1c3c](https://github.com/phodal/shire/commit/16d1c3c93e45304038ae5d4a726097f167dd425c))
- **saql:** refactor Saql language support and add new classes [#16](https://github.com/phodal/shire/issues/16) ([cad778d](https://github.com/phodal/shire/commit/cad778dd28a51b8d5d931af54378189ec7df031f))
- **saql:** refactor Saql language support and add new classes [#16](https://github.com/phodal/shire/issues/16) ([0b00f60](https://github.com/phodal/shire/commit/0b00f60c65bac04b4bf80b095da22817ae638825))
- **shirelang:** add ShireCommenter and update lexer and parser [#16](https://github.com/phodal/shire/issues/16) ([916a7d7](https://github.com/phodal/shire/commit/916a7d758e09a77615f9f34f894914f882df6be4))
- **shirelang:** enhance FrontMatterType subclasses and refactor pattern action processing [#18](https://github.com/phodal/shire/issues/18) ([4777ee3](https://github.com/phodal/shire/commit/4777ee34b2cb65c26c7d506a25d694bb67c90b8b))
- **shirelang:** improve query expression and statement processing [#16](https://github.com/phodal/shire/issues/16) ([62cd708](https://github.com/phodal/shire/commit/62cd708d59179e40407c0bda184eb82baf7968a2))
- **shirelang:** refactor methods and improve error handling [#18](https://github.com/phodal/shire/issues/18) ([9b170ee](https://github.com/phodal/shire/commit/9b170ee80e973f6fe5f6b3c93c684290826210b5))
- **ShireLang:** update grammar rules and lexer definitions [#16](https://github.com/phodal/shire/issues/16) ([29cc1b1](https://github.com/phodal/shire/commit/29cc1b1364981f6548c08b4a7933ffc63d96fa09))
- **syntax-highlighter:** add new keywords to ShireSyntaxHighlighter [#16](https://github.com/phodal/shire/issues/16) ([9d937ec](https://github.com/phodal/shire/commit/9d937ec7dd5d7b1424f5ef6814af09f711cc9c68))
- **testing:** add DefaultShireSymbolProvider and update ShireQueryExpressionTest ([e756f77](https://github.com/phodal/shire/commit/e756f77af2a8b23b6b3ac136982b5a234b82d148)), closes [#16](https://github.com/phodal/shire/issues/16)
- **variable-resolver:** add date and time to SystemInfoVariableResolver ([bc3356b](https://github.com/phodal/shire/commit/bc3356b694f63036fb2b06860b3631097a87966f))
- **variable-resolver:** refactor variable resolution system [#18](https://github.com/phodal/shire/issues/18) ([a96c00e](https://github.com/phodal/shire/commit/a96c00ee704f545c270ef1a2333ffab33ab5904c))
- **VariablePatternActionExecutor:** add project, editor, and hole as class properties [#18](https://github.com/phodal/shire/issues/18) ([54da7a6](https://github.com/phodal/shire/commit/54da7a6d47c60b4c1cce75a9d833049b745c24c1))
- **variable:** refactor PsiVariable to PsiContextVariable [#18](https://github.com/phodal/shire/issues/18) ([6e5e176](https://github.com/phodal/shire/commit/6e5e176ef30e4ae83a93bfba07386dfb4773812d))

### Performance Improvements

- **build:** optimize Gradle build performance in GitHub Actions ([f09254d](https://github.com/phodal/shire/commit/f09254d1bd81a47a47794545d5abb9e0988b78bc))

## [0.0.6](https://github.com/phodal/shire/compare/v0.0.4...v[0.0.6]) (2024-06-16)

### Bug Fixes

- **java:** fix null check for JavaSdkType in JavaToolchainProvider ([fb8ad5f](https://github.com/phodal/shire/commit/fb8ad5f88d980f14505f8af1eb798de733438b81))
- **java:** handle null psiElement in resolveVariableValue ([ba34428](https://github.com/phodal/shire/commit/ba34428e95ca67e1373853be6e590d5ed4cf1eb9))
- **test:** update file patterns in test data ([bfbe463](https://github.com/phodal/shire/commit/bfbe46370b41a4c7f2c10f8d48f3446c97866b47))

### Features

- **actions:** add WhenConditionValidator for dynamic actions ([2e1c06a](https://github.com/phodal/shire/commit/2e1c06a1088c0c69cb471e4ae90af020f9147c36))
- **compiler:** add support for method call with arguments ([1cb74e3](https://github.com/phodal/shire/commit/1cb74e3771f265266480fa35daa18053630883ff))
- **compiler:** update HobbitHole variables to use list of conditions ([dd9dc0a](https://github.com/phodal/shire/commit/dd9dc0ae5638f0693dd3c651705d5da6e17f0dce))
- **completion:** add support for when condition functions ([35e13aa](https://github.com/phodal/shire/commit/35e13aa45c33886252ed31f9cde1af2accd154ff))
- **core:** add CodeStructVariableProvider for code struct generation [#14](https://github.com/phodal/shire/issues/14) ([9a6c573](https://github.com/phodal/shire/commit/9a6c5739066fd4cd2f0a4f61e0683919d89bb6b8))
- **frontmatter:** add support for logical OR expressions in frontmatter parsing ([3c0f5dc](https://github.com/phodal/shire/commit/3c0f5dc9e48aa7c4ec296cfab95cd5a5e59d3f29))
- **grammar:** add support for velocity expressions ([c71fb07](https://github.com/phodal/shire/commit/c71fb07f638afa52279cbc4921733c17c52d6cc7))
- **highlight:** add 'WHEN' keyword support ([abdcb6c](https://github.com/phodal/shire/commit/abdcb6cbfc46d5a12f81ff631304b47cac36a392))
- **java:** add JavaCodeStructVariableProvider for code struct generation [#15](https://github.com/phodal/shire/issues/15) ([378f44e](https://github.com/phodal/shire/commit/378f44ec4a4f4779cbdcc0329568257e1a519adb))
- **runner:** add SymbolResolver for variable resolution [#14](https://github.com/phodal/shire/issues/14) ([ed03e25](https://github.com/phodal/shire/commit/ed03e25211dc222a92dca99269150af5a4fbf351))
- **search:** add TfIdf class for text analysis [#14](https://github.com/phodal/shire/issues/14) ([55e94a6](https://github.com/phodal/shire/commit/55e94a6f109dd4677f985f605119d133045c75c6))
- **shire:** add PatternFun Cat subclass ([ca2b531](https://github.com/phodal/shire/commit/ca2b53117d2ca0c512bf182d8d83fb55922ee8bb))
- **template:** add Shire Action template and action [#14](https://github.com/phodal/shire/issues/14) ([0493d23](https://github.com/phodal/shire/commit/0493d2363006ae25073272d8035d0851aee15824))
- **tokenizer:** add TermSplitter and StopwordsBasedTokenizer [#14](https://github.com/phodal/shire/issues/14) ([a774f48](https://github.com/phodal/shire/commit/a774f48a17c64553ab417763511b230dfaf73b18))

## [0.0.4](https://github.com/phodal/shire/compare/2b4a6f06733149d0cd9763e2d4719a048fa37ce3...v[0.0.4]) (2024-06-11)

### Bug Fixes

- **build:** remove unnecessary project dependency ([4628e8b](https://github.com/phodal/shire/commit/4628e8b8b992b9904f68fd46d5c0117af9c51418))
- **build:** update paths for plugin directory in workflows ([a0b6570](https://github.com/phodal/shire/commit/a0b657083ec8d49d65dff87f4efd003848aac7ed))
- **parser:** fix filenameRules regex in ShireFmObject test ([85de5bd](https://github.com/phodal/shire/commit/85de5bdf5964886d6568aee24b61a1ff2e873da2))
- **parser:** fix filenameRules regex in ShireFmObject test ([74783a1](https://github.com/phodal/shire/commit/74783a1d7dfb15b956132f68e5316ba9775c0512))
- **release:** update gradle task path for patching changelog ([201c458](https://github.com/phodal/shire/commit/201c458e09399435039ade13fac81a0d43d26754))

### Features

- **action:** add ShireAction and ShireActionRegister interfaces ([e5ed7b8](https://github.com/phodal/shire/commit/e5ed7b8577e577717c26bb44520e4099dc57cb37))
- **actions:** add context and intent action retrieval ([1be1fda](https://github.com/phodal/shire/commit/1be1fda1b58f6d1e8f7a9e535ab1fedff0c17a15))
- **agent:** add custom agent response actions and configurations ([d6b7501](https://github.com/phodal/shire/commit/d6b7501a707c0c0172c6fc2a44db8dfc51a03569))
- **codeedit:** add CodeModifier interface for code editing ([3bcdf44](https://github.com/phodal/shire/commit/3bcdf44b35840e90af31045c0a387c93fbf2f38a))
- **codemodel:** add ClassStructureBuilder, ClassStructureProvider, and FormatableElement ([5504468](https://github.com/phodal/shire/commit/55044689e759e1ed7de6f4c1ca9c71ae19cee83e))
- **compiler:** add ElementStrategy for auto selecting parent block element ([4f5118b](https://github.com/phodal/shire/commit/4f5118b68a75af91262a2d5540e83c9dd3a1e920))
- **compiler:** add FrontmatterParser for dynamic action configuration ([12bb82a](https://github.com/phodal/shire/commit/12bb82a23275910b9195655ae9cdee16462a585c))
- **compiler:** add ShellRunService for running shell scripts ([63ab840](https://github.com/phodal/shire/commit/63ab840a6844d7a7a608aa11126da80863603a10))
- **compiler:** add support for browsing URLs ([3b63e52](https://github.com/phodal/shire/commit/3b63e52e2ff4debc066c6ae17de5ae4218bc9edd))
- **compiler:** add support for DATE type in front matter ([3062445](https://github.com/phodal/shire/commit/3062445a58cba099b1c3031a72bf8ec7247132f4))
- **compiler:** add support for filename rules ([c9e860c](https://github.com/phodal/shire/commit/c9e860c9bd14f7b8dc88b0f24792c1aacba19c56))
- **compiler:** add support for front matter configuration ([aada8af](https://github.com/phodal/shire/commit/aada8afdd3efaeb25504e3f7aa81350cb1c11080))
- **compiler:** add support for frontmatter parsing ([b80cee1](https://github.com/phodal/shire/commit/b80cee1da7c9e1a629777abcd4e9e202799e6898))
- **compiler:** add support for head and tail functions ([b3f0aa0](https://github.com/phodal/shire/commit/b3f0aa024408ff8add6091b8f2f2204d5b538aaf))
- **compiler:** add support for loading custom agents ([62357fe](https://github.com/phodal/shire/commit/62357feadc08f20ccfc3665e3b8498b65d4a3383))
- **compiler:** add support for print function ([511cdde](https://github.com/phodal/shire/commit/511cddef30a3d4169cb1679a0ecd2623da082d02))
- **compiler:** add support for replace pattern function ([9d4160f](https://github.com/phodal/shire/commit/9d4160f75700f130370a5112c4e1e3f1f43f6b1c))
- **compiler:** add support for variables in HobbitHole ([0178647](https://github.com/phodal/shire/commit/01786479ab1b3c524489afcbd12cc2bd6cc8ed47))
- **completion:** add basic completion for project run tasks ([e8c6578](https://github.com/phodal/shire/commit/e8c6578c4ce9a06a567bf4301b50041792286f61))
- **completion:** add completion providers for code fence languages, file references, variables, agents, commands, and more ([4ef7ab4](https://github.com/phodal/shire/commit/4ef7ab49a09f58fdf9dbd379e41f64f16dad7aaa))
- **completion:** add completion support for Git revisions ([c977564](https://github.com/phodal/shire/commit/c9775640d40ae7818a2b8c339195a22e50dbc7af))
- **completion:** add Hobbit Hole code completion ([4fd6e41](https://github.com/phodal/shire/commit/4fd6e419b9ece791a1194a6dd17a256d56e944b2))
- **core:** add commit functionality to RevisionProvider ([7be17f1](https://github.com/phodal/shire/commit/7be17f1cb0136bb177a478a5bc1a51e3cf91d543))
- **core:** add DAG support with topological sort and cycle detection ([c01ada5](https://github.com/phodal/shire/commit/c01ada5760574a31ada3b6b9c8598a4840ff5cf7))
- **core:** add file filtering functionality ([8864237](https://github.com/phodal/shire/commit/88642371187671d02f3fc95335a8b1fd88073d88))
- **core:** add Java build system provider ([5afd1ba](https://github.com/phodal/shire/commit/5afd1ba4c2c81722a488f454130d13fca996f2c4))
- **core:** add ProjectRunService interface and extension point ([71d02e1](https://github.com/phodal/shire/commit/71d02e1fa5e7f5ed01c243f02fd68859dd5485b2))
- **core:** add ToolchainProvider extension point ([6a6d1d8](https://github.com/phodal/shire/commit/6a6d1d8a679e15694618b5043311714027dda14d))
- **docs:** add roadmap section to README ([e2def4d](https://github.com/phodal/shire/commit/e2def4d965b5dace4c3978dce269426df39dd401))
- **docs:** add support for frontmatter in code highlighting ([dbf5765](https://github.com/phodal/shire/commit/dbf5765e5eb9b4b76f2b27e625065af063dfb78f))
- **frontmatter:** add filename and file content filters ([be33598](https://github.com/phodal/shire/commit/be3359814375937a125fbe25efd7f01582dfc8c0))
- **git:** add GitQuery and VcsPrompting classes ([f5d5fe7](https://github.com/phodal/shire/commit/f5d5fe743edcef610e14b5ffb902856035faecb7))
- **git:** add GitQuery and VcsPrompting classes ([eddfe37](https://github.com/phodal/shire/commit/eddfe372741acc6fac73c39f78ad9b22763806cf))
- **grammar:** add qualRefExpr to ShireParser.bnf ([e5bf261](https://github.com/phodal/shire/commit/e5bf26123704d8ade8f2302925aa60cb18854a82))
- **grammar:** add support for 'when' keyword in condition expressions ([ee3dc7e](https://github.com/phodal/shire/commit/ee3dc7ee33568d8265bfc07d368c9ee6a5148930))
- **grammar:** add support for pattern actions ([9471994](https://github.com/phodal/shire/commit/947199424eb5f6c5354d278500c65e7dfa132343))
- **grammar:** extend grammar for expressions and operators ([0aa661f](https://github.com/phodal/shire/commit/0aa661fc31934df8661f288c2efbbc300c7a5a6d))
- **hobbit:** add support for action location in HobbitHole ([3c530dd](https://github.com/phodal/shire/commit/3c530ddd20b938454e77edc22c069e2d1aec6dea))
- **hobbit:** add support for selection strategy ([0b4e090](https://github.com/phodal/shire/commit/0b4e090472f418023342460f4905bdcba9fb3326))
- **httpclient:** add HttpClientRunService for HTTP requests ([8a33137](https://github.com/phodal/shire/commit/8a33137a00783c4c110b00e810f8a08b1925be9e))
- **httpclient:** add support for REST client plugin ([994ff0e](https://github.com/phodal/shire/commit/994ff0e142419d33951036e9bce3300b57b94fc8))
- **index:** add ShireIdentifierIndex for file content ([62891a0](https://github.com/phodal/shire/commit/62891a0d0c5ffc8888515ff2471fdcab41c6b930))
- **intention:** add Shire Assistant with AI AutoAction ([0b23d35](https://github.com/phodal/shire/commit/0b23d3584dbb87b193aedd14a8d93bd4288051f5))
- **intention:** add Shire Hobbit AI action support ([c1122e1](https://github.com/phodal/shire/commit/c1122e182d5deb5da52f061177e5f1a34f60511d))
- **java:** add Java element strategy builder ([a4d571a](https://github.com/phodal/shire/commit/a4d571ac2a3b6bb41cb6af23c80955ff7d1e1b46))
- **java:** add Java symbol provider implementation ([76c2042](https://github.com/phodal/shire/commit/76c2042268e2e5f92b7ff261dbe8c793269891ed))
- **java:** add Java toolchain provider ([864c1cb](https://github.com/phodal/shire/commit/864c1cb6ca6af4088dcdbb586954af8d72174f70))
- **java:** add method to find nearest target element ([3cf769c](https://github.com/phodal/shire/commit/3cf769c0c41efd8fe001cce91e5bc4a97e3d1df3))
- **java:** add method to get run task name ([4e4ab8a](https://github.com/phodal/shire/commit/4e4ab8a1b107a0d0eeb5c02dbd67d39272affaab))
- **java:** add MvcContextService and ControllerContext ([b5edce5](https://github.com/phodal/shire/commit/b5edce576ef24eae8892a8e1b3b09ff1acfa8b91))
- **language:** add Shire language injector, folding builder, and completion contributor ([8f5ca99](https://github.com/phodal/shire/commit/8f5ca99d51554dd0d93da3a62fe50e9ee8324bef))
- **language:** add Shire language support ([8332a3c](https://github.com/phodal/shire/commit/8332a3c93dd0c922e118694089264b68d3d312a8))
- **language:** add Shire language support ([f351552](https://github.com/phodal/shire/commit/f3515520c86f99c8b0387614f86cc93a2e87bfa5))
- **llm:** add MockProvider for testing ([089d496](https://github.com/phodal/shire/commit/089d4968d9fa6c44314a0f21c393de96808ce863))
- **llm:** add OpenAI LLM provider and settings ([8518bd3](https://github.com/phodal/shire/commit/8518bd3360b2dd1d40bc688c3409419b4acc4aa6))
- **middleware:** add CodeVerifyProcessor for syntax error checking ([1bec4c3](https://github.com/phodal/shire/commit/1bec4c33f07be7cbc3f516e4ce57cb216d9b76d1))
- **middleware:** add post processor support ([0a2aeea](https://github.com/phodal/shire/commit/0a2aeeaf5ec9407cbf95620a3303373e3a886beb))
- **middleware:** add PostCodeHandler interface and PostCodeHandle enum ([9d734c5](https://github.com/phodal/shire/commit/9d734c54d475a887df34ebb2ade8a429857a1d28))
- **middleware:** add TimeMetricProcessor for measuring code execution time ([2c3b1a6](https://github.com/phodal/shire/commit/2c3b1a6c9589aea733666bc533dc130747fea5da))
- **modify:** add ShireModificationListener for file modification ([c61f973](https://github.com/phodal/shire/commit/c61f97347945ed6d2610608289c74537909bdf43))
- **modify:** add ShireModificationListener for file modification ([230b00e](https://github.com/phodal/shire/commit/230b00e9d0b36c82a36da3b3bf861845faeb439c))
- **parser:** add new test data files and refactor code ([b666fc6](https://github.com/phodal/shire/commit/b666fc6d3a9d51ab17972082227eeee6a5ba32d6))
- **parser:** add support for front matter parsing ([01ef64e](https://github.com/phodal/shire/commit/01ef64ebaed4951e32892b8d0bf0704b0974c994))
- **parser:** add support for pattern actions ([bb30004](https://github.com/phodal/shire/commit/bb30004e14684c9127eb138167336e5b4fd93d87))
- **parser:** add support for pattern element ([12fb06e](https://github.com/phodal/shire/commit/12fb06e83338c143b8614a0ad27485c1c0c1d2c7))
- **pattern:** add pattern actions for filtering, sorting, and executing commands ([be08b28](https://github.com/phodal/shire/commit/be08b28353dfbd940c3c585bd1a2f1fe1f7563f6))
- **project:** add core and language modules ([2b4a6f0](https://github.com/phodal/shire/commit/2b4a6f06733149d0cd9763e2d4719a048fa37ce3))
- **provider:** add AutoTesting provider for unit testing ([2fb10e2](https://github.com/phodal/shire/commit/2fb10e26c27dbd4d2d2f2090c84827dae5a37680))
- **provider:** add PsiElementStrategyBuilder interface ([c5ca72b](https://github.com/phodal/shire/commit/c5ca72b86b4fa84417bf7646500b1cb4ddf52da4))
- **provider:** add RevisionProvider interface and implementation ([fa05221](https://github.com/phodal/shire/commit/fa05221654c53c7821e5de8ff148d62c6c0a9b3c))
- **provider:** add RunService interface and implementation ([a509717](https://github.com/phodal/shire/commit/a5097170adb1efc848aa027761613b213f0c39b8))
- **psi:** add method to get relative PsiElement with PsiComment ([ba3b521](https://github.com/phodal/shire/commit/ba3b5210647242802c6c66b0fb968344345f674c))
- **run:** add Shire program runner and process processor ([554c39f](https://github.com/phodal/shire/commit/554c39f8784773a53063e5eab9246e4b4d87a8aa))
- **runner:** add support for running tasks in projects ([586a9d6](https://github.com/phodal/shire/commit/586a9d6bcfc5979fd12d5b2089eb339ae03238f5))
- **settings:** add LlmCoroutineScope, CustomAgent loadFromProject, and TestConnection ([efdc2f9](https://github.com/phodal/shire/commit/efdc2f99ff0739edb0a9d40a62404a68defece37))
- **settings:** add Shire settings configurable UI ([dba5d68](https://github.com/phodal/shire/commit/dba5d68ec0f969ebce518ab573d3471de3b2fe10))
- **shell:** add shell language support plugin file ([bdc1c90](https://github.com/phodal/shire/commit/bdc1c90f10d7c2c4fb8f7157db343dd756c11352))
- **shire:** add Shire context action group and location support ([e3df86d](https://github.com/phodal/shire/commit/e3df86d16d0c9985fec787a8e76ce553be9a9a45))

[Unreleased]: https://github.com/phodal/shire/compare/v0.4.8...HEAD
[0.4.8]: https://github.com/phodal/shire/compare/v0.4.7...v0.4.8
[0.4.7]: https://github.com/phodal/shire/compare/v0.4.6...v0.4.7
[0.4.6]: https://github.com/phodal/shire/compare/v0.4.5...v0.4.6
[0.4.5]: https://github.com/phodal/shire/compare/v0.4.3...v0.4.5
[0.4.3]: https://github.com/phodal/shire/compare/v0.4.2...v0.4.3
[0.4.2]: https://github.com/phodal/shire/compare/v0.4.1...v0.4.2
[0.4.1]: https://github.com/phodal/shire/compare/v0.0.8...v0.4.1
[0.0.8]: https://github.com/phodal/shire/compare/v0.0.7...v0.0.8
[0.0.7]: https://github.com/phodal/shire/compare/v0.0.6...v0.0.7
[0.0.6]: https://github.com/phodal/shire/compare/v0.0.4...v0.0.6
[0.0.4]: https://github.com/phodal/shire/commits/v0.0.4
