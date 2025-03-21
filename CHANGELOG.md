# [](https://github.com/phodal/shire/compare/v1.3.4...v) (2025-02-05)

## [Unreleased]

## [1.3.4](https://github.com/phodal/shire/compare/v1.3.3...v[1.3.4]) (2025-02-05)

### Features

* **command:** add isApplicable method to ShireCommand ([4857af8](https://github.com/phodal/shire/commit/4857af8de1afb8301881ecaf71c5f0fed3145bd5))
* **commands:** add commandName property to all ShireCommand implementations ([08fc7f3](https://github.com/phodal/shire/commit/08fc7f38a14151d2b9d8444829d21065812758b7))
* **commands:** enhance command descriptions and add enableInSketch flag ([0f88b46](https://github.com/phodal/shire/commit/0f88b4661598efb0a8577d47a45cc827e251e958))
* **database:** add schema function and simplify toolchain provider ([05acdae](https://github.com/phodal/shire/commit/05acdaedd0312df00d08a99c7a8180208af0f3b7))
* **search:** add ripgrep search command ([bafff1b](https://github.com/phodal/shire/commit/bafff1b3747ef7d9cc4cc9073d3987a36adeff6c))
* **terminal:** add terminal sketch provider for bash support ([1148dc8](https://github.com/phodal/shire/commit/1148dc868a3c8cc71ac98be4caca00eac6500a28))

## [1.3.3](https://github.com/phodal/shire/compare/v1.3.2...v[1.3.3]) (2025-01-14)

### Bug Fixes

* **compiler:** update file validation in LocalSearchInsCommand ([fb26400](https://github.com/phodal/shire/commit/fb264003c10e968d8177a1c11d2543e651c9d1f8))
* **run-code:** wrap scratch file deletion in runWriteAction ([5271cd7](https://github.com/phodal/shire/commit/5271cd7072d1ac33535c07ecaeda5fb229e1edfd))

### Features

* **commands:** add local search, related symbol, and open file commands ([7e7de3b](https://github.com/phodal/shire/commit/7e7de3b5174e8b6f9959ad5ceff28684a37d426e))
* **editor:** Add file selection support in preview editor ([07140f3](https://github.com/phodal/shire/commit/07140f3597d46a62a08dcf8feb1e7fe7361fe507))
* **kotlin:** add KotlinPsiContextVariableProvider ([512cb3a](https://github.com/phodal/shire/commit/512cb3a11c088ac9dee1791ef28afc8e2dc75792))
* **markdown:** add support for Shire code fence parsing ([b72753c](https://github.com/phodal/shire/commit/b72753c50581c4eb0cb24acefecda3907d0dad39))
* **parser:** add support for `DIR` command to list directory contents ([8d0ddc9](https://github.com/phodal/shire/commit/8d0ddc940e36245f6e049e60a5e34afe9a87a3dc))
* **streaming:** integrate OnStreamingService into ShireInlineChat ([23858b1](https://github.com/phodal/shire/commit/23858b103a00d3b11abb62741ebb3c045a5a45b6))

## [1.3.2](https://github.com/phodal/shire/compare/v1.3.1...v[1.3.2]) (2025-01-09)

### Bug Fixes

* **debugger:** restrict debug/run execution based on executor ID [#183](https://github.com/phodal/shire/issues/183) ([47d2b71](https://github.com/phodal/shire/commit/47d2b7106a3b94d2cffcc94428b23ce8cb62d885))

### Features

* **debugger:** add debug tab layout and session listener [#183](https://github.com/phodal/shire/issues/183) ([2053470](https://github.com/phodal/shire/commit/20534702c693ba2a72e3e8d22ab68b5672658fbd))
* **debugger:** add Shire debugger support [#183](https://github.com/phodal/shire/issues/183) ([3b1fd51](https://github.com/phodal/shire/commit/3b1fd51d6de03ba8e41b60201e6e7b2d00596b78))
* **debugger:** enhance debug process with variable snapshot support [#183](https://github.com/phodal/shire/issues/183) ([184e691](https://github.com/phodal/shire/commit/184e69186e765f727bcd1bbfe71c8fa29c71e034))
* **debugger:** enhance debugger with breakpoint handling and UI improvements [#183](https://github.com/phodal/shire/issues/183) ([743f4ca](https://github.com/phodal/shire/commit/743f4ca1c2f8181e7edb6271265c051b0faca580))
* **debugger:** enhance stack frame presentation with custom variables [#183](https://github.com/phodal/shire/issues/183) ([bfc1f10](https://github.com/phodal/shire/commit/bfc1f10ce65ced92b28e821c97316e24cb0e378d))

### Reverts

* Revert "refactor(debugger): remove unused ShireDebugSettings file #183" ([c7f6f14](https://github.com/phodal/shire/commit/c7f6f1424298d5d0c2ae0f54a5ceb670a2110698)), closes [#183](https://github.com/phodal/shire/issues/183)
* Revert "refactor(debugger): remove unused ShireDebugSettings configuration" ([358a343](https://github.com/phodal/shire/commit/358a34383d75ed7706d2d49cfbe9a6e4a650ad83))

## [1.3.1](https://github.com/phodal/shire/compare/v1.3.0...v[1.3.1]) (2025-01-06)

### Bug Fixes

* **build:** disable prepareTestSandbox task ([0140675](https://github.com/phodal/shire/commit/014067564ce8801c1b5a020c9200e160e7c3d4b2))
* **code-highlight:** normalize line separators in text replacement [#157](https://github.com/phodal/shire/issues/157) ([1a4ddd4](https://github.com/phodal/shire/commit/1a4ddd4f458e2c6d36d1e6b23f82ac993dfd5379))
* **compatibility:** refactor database session handling ([0d4885e](https://github.com/phodal/shire/commit/0d4885e701d7c1c0da99d4bacb174f8da50c6eb6))
* **compatibility:** remove deprecated client property setting ([f0b2774](https://github.com/phodal/shire/commit/f0b27741779d7e93195304928a88dda4f38ab466))
* **editor:** add missing row for highlight sketch [#178](https://github.com/phodal/shire/issues/178) ([3d9f6cb](https://github.com/phodal/shire/commit/3d9f6cb17496f8fd0322f7c957da186b0ff4a7a8))
* **editor:** set PlainTextLanguage for CodeHighlightSketch [#157](https://github.com/phodal/shire/issues/157) ([6fd404f](https://github.com/phodal/shire/commit/6fd404f04b53c5a844352a6c9f8bf60141f3cf72))
* **editor:** update sample file label format [#178](https://github.com/phodal/shire/issues/178) ([8c68528](https://github.com/phodal/shire/commit/8c68528ce1f3b93e7345247552ce3d12ccfafb5d))
* **injection:** change error log to warn for language injection ([6cf0cda](https://github.com/phodal/shire/commit/6cf0cda4048f31d6e1f4a768148509fc7e250704))
* **input:** ensure safe removal of elements from listModel ([a433c42](https://github.com/phodal/shire/commit/a433c428b260cf4cdab3bd173e6d95030758f48b))
* **input:** Resolved close button functionality in chat box ([5d66d68](https://github.com/phodal/shire/commit/5d66d688d1574cbfbdbe919095b3f022909dbdc4))
* **input:** skip invalid psiFile in ShireInput ([c3c49a8](https://github.com/phodal/shire/commit/c3c49a8137503a21998ae687d79700c7b5c54603))
* **input:** skip invalid psiFile in ShireInput ([0b0f011](https://github.com/phodal/shire/commit/0b0f0110d2cdd84dca09a8c6445833902d51699f))
* **python:** restore PythonCore plugin and simplify PsiUtil check ([63f73e5](https://github.com/phodal/shire/commit/63f73e5102eb05def5b85c88a670cad7481b0bde))
* **tests:** update file extension and add error handling ([eae3a8c](https://github.com/phodal/shire/commit/eae3a8c0d7eeba6f540e3f19576a88ef9038e9c8))
* **ui:** execute table update on UI thread ([9bec8f3](https://github.com/phodal/shire/commit/9bec8f363a11ef824d7516168a8739b92bfe072d))

### Features

* **chat:** add inline chat provider support [#157](https://github.com/phodal/shire/issues/157) ([594d985](https://github.com/phodal/shire/commit/594d985c19388b716f6133163cd4e07e24e396a6))
* **chat:** add prompt method to inline chat service [#157](https://github.com/phodal/shire/issues/157) ([ab09b82](https://github.com/phodal/shire/commit/ab09b822be0af908d5441731d2a19a9a4c56d32d))
* **chat:** enhance inline chat UI and interaction [#157](https://github.com/phodal/shire/issues/157) ([c85ff46](https://github.com/phodal/shire/commit/c85ff467ada569b28ef0c6acb434130cd1665736))
* **compiler:** add database command support [#271](https://github.com/phodal/shire/issues/271) ([6f079b4](https://github.com/phodal/shire/commit/6f079b4b6cebc53d1064f0d4f6dbf1cb02ed427d))
* **compiler:** add ResolvableVariableSnapshot for variable tracking [#178](https://github.com/phodal/shire/issues/178) ([22131dd](https://github.com/phodal/shire/commit/22131dd3a890e54d62763b02653eb6c30215ec62))
* **compiler:** add ResolvableVariableSnapshot for variable tracking [#183](https://github.com/phodal/shire/issues/183) ([c6f319a](https://github.com/phodal/shire/commit/c6f319a06817d9cf512d4511cc3e299e82362984))
* **completion:** enhance custom agent completion with auto-insert ([146ecc4](https://github.com/phodal/shire/commit/146ecc4222bf0dfcc59eb933a93cb13877d4d9a0))
* **database:** add SQL query execution support [#171](https://github.com/phodal/shire/issues/171) ([660eb67](https://github.com/phodal/shire/commit/660eb674116ec5b73a1347871977f2a8271d270a))
* **editor:** add document listener for real-time updates [#157](https://github.com/phodal/shire/issues/157) ([68c88ba](https://github.com/phodal/shire/commit/68c88ba638628c14876503f43c35da746d4a22f5))
* **editor:** add gutter icon handler for editor selections [#157](https://github.com/phodal/shire/issues/157) ([bf85b08](https://github.com/phodal/shire/commit/bf85b0802a7604ddce409dc1f67affc88e213f58))
* **editor:** add ShireFileEditorProvider for custom file editing [#178](https://github.com/phodal/shire/issues/178) ([a309063](https://github.com/phodal/shire/commit/a3090638501c360afeebaf505222328d8d9c1959))
* **editor:** add ShirePreviewEditorProvider and enhance editor creation [#157](https://github.com/phodal/shire/issues/157) ([99058b6](https://github.com/phodal/shire/commit/99058b6b03e6749addc3823c1224381c75de4419))
* **editor:** add ShireVariablePanel for variable display [#157](https://github.com/phodal/shire/issues/157) ([ff245f5](https://github.com/phodal/shire/commit/ff245f5f927e14aee99d5574aafec2ed8c159947))
* **editor:** enhance variable debug panel UI and functionality [#157](https://github.com/phodal/shire/issues/157) ([c51b388](https://github.com/phodal/shire/commit/c51b388178b370be375ea9d6e048c4dbe2ad5bdb))
* **editor:** improve UI and async handling ([42e350f](https://github.com/phodal/shire/commit/42e350fdfe3147c73ffab7216490b8a16e0bc7a8)), closes [#178](https://github.com/phodal/shire/issues/178)
* **editor:** replace ShireFileEditor with ShireSplitEditor [#157](https://github.com/phodal/shire/issues/157) ([4b51084](https://github.com/phodal/shire/commit/4b51084becc30eb0c2235cda3f1ab2d9bb1336b0))
* **editor:** replace variable panel with table and add refresh action [#157](https://github.com/phodal/shire/issues/157) ([1dca503](https://github.com/phodal/shire/commit/1dca503ed88d91bc6fd0b816ef350f17cf163eb0))
* **inline-chat:** add inline chat service and integrate with gutter icon [#157](https://github.com/phodal/shire/issues/157) ([933e8d7](https://github.com/phodal/shire/commit/933e8d79e00eca2eced45f419b757381ad5cac17))
* **inline-chat:** add LLM integration and input handling [#157](https://github.com/phodal/shire/issues/157) ([13b49e2](https://github.com/phodal/shire/commit/13b49e2f59da1d4538d16d065daba19702b983cd))
* **inline-chat:** enhance inline chat panel with improved UI and state management [#157](https://github.com/phodal/shire/issues/157) ([a9cfd60](https://github.com/phodal/shire/commit/a9cfd60982a105c0995bb6f1cff73f6005e2b837))
* **inline-chat:** enhance prompt handling with dynamic actions [#157](https://github.com/phodal/shire/issues/157) ([97ce772](https://github.com/phodal/shire/commit/97ce772b52f0f81cf42532c2b179a7537340dcbf))
* **java:** add test file lookup for related classes ([680c804](https://github.com/phodal/shire/commit/680c8040b1947b40dfcac7e367da20cd1a19397b))
* **kotlin:** add Kotlin language support for testing, refactoring, and related classes ([9f55993](https://github.com/phodal/shire/commit/9f5599354f8f34fb13ae3245dc81b49f51524e4c))
* **parser:** add DATABASE command to code block checks [#171](https://github.com/phodal/shire/issues/171) ([8339428](https://github.com/phodal/shire/commit/83394289f9ae052d815bda627cf6b89911369f4b))
* **prompt:** add ShirePromptBuilder for dynamic prompt generation [#157](https://github.com/phodal/shire/issues/157) ([df07fcc](https://github.com/phodal/shire/commit/df07fcc4109dbd4506c5c913f111b3e1db837e54))
* **runner:** add streaming service support in ShireRunner [#157](https://github.com/phodal/shire/issues/157) ([58124a3](https://github.com/phodal/shire/commit/58124a3061f64f292c7e40182d651bd80383639c))
* **runner:** add support for custom editor in ShireRunner [#178](https://github.com/phodal/shire/issues/178) ([dd139d2](https://github.com/phodal/shire/commit/dd139d25ee5ba75193010cc7c5da82fd6ab2b874))
* **sonarlint:** move SonarLint listener to thirdparty package [#157](https://github.com/phodal/shire/issues/157) ([b3c1530](https://github.com/phodal/shire/commit/b3c15304acd7ba22fe335dac8c096b9945cc985e))
* **sse:** enhance custom request body handling ([5a424fb](https://github.com/phodal/shire/commit/5a424fbf5dfc65cc78a718b38bc4089c7669591c))
* **ui:** adjust editor and panel border styles [#157](https://github.com/phodal/shire/issues/157) ([ae76259](https://github.com/phodal/shire/commit/ae762595c719dca22456ac5349a54b1c86e604cf))
* **ui:** enhance border styling and add submit button [#157](https://github.com/phodal/shire/issues/157) ([982db35](https://github.com/phodal/shire/commit/982db35ba49ff2032cefb3b24ea506c363128f06))

### Reverts

* Revert "chore(gradle): update Gradle wrapper  to 8.12 and try to resolve GitHub Action issue and add SPDX license" ([fcad6ba](https://github.com/phodal/shire/commit/fcad6ba533cbb0bdce9e07fb334ea826df5f3e74))

## [1.2.4](https://github.com/phodal/shire/compare/v1.2.3...v[1.2.4]) (2024-12-26)

### Bug Fixes

* **input:** skip unsupported languages in selectionChanged [#170](https://github.com/phodal/shire/issues/170) ([cd182d3](https://github.com/phodal/shire/commit/cd182d3fd91a3ae945024e11c420561bf337d548))

### Features

* **compiler:** add file path comment in code block output [#170](https://github.com/phodal/shire/issues/170) ([81a93dd](https://github.com/phodal/shire/commit/81a93dd295f9f9d53663d90549e55ffe06b7e253))
* **completion:** add priority to file reference completion elements ([c97be93](https://github.com/phodal/shire/commit/c97be93ef4fe596214ab3ab8d7fced8e927407e2))
* **completion:** add ShireCompletionLookupActionProvider and ShireLookupElement ([8ff9ee3](https://github.com/phodal/shire/commit/8ff9ee3669eed108d497338719a23fad3c79f9e5))
* **completion:** enhance lookup functionality and refactor dependencies ([38b2f3d](https://github.com/phodal/shire/commit/38b2f3d13a8036a5d0e51efb782b48c29c96896f))
* **core:** add related classes provider and lookup functionality ([e97e828](https://github.com/phodal/shire/commit/e97e82831925c5daece6688a8a3c16813731c0af))
* **core:** add relative file path and caching for related classes [#170](https://github.com/phodal/shire/issues/170) ([58858b9](https://github.com/phodal/shire/commit/58858b913c056397a175437c862fb9ca38b47c15))
* **diff:** add openDiffView method and refactor diff panel creation ([acf2245](https://github.com/phodal/shire/commit/acf22455d8e99be3ba43873cc57d87aa54b88c1d))
* **diff:** enhance diff view and prevent duplicate entries ([61795f2](https://github.com/phodal/shire/commit/61795f2dd02b5ebb49fdcb592050deba11c354f6))
* **diff:** try to add for local compare view ([8d6e4c1](https://github.com/phodal/shire/commit/8d6e4c1e39427c0230c9c780e0222d14d59a0d4c))
* **folding:** add file command support to folding builder ([b502605](https://github.com/phodal/shire/commit/b5026055ca824d493a542f552739e6557e595485))
* **input:** add editor and related listeners to ShireInput [#170](https://github.com/phodal/shire/issues/170) ([9e71d1e](https://github.com/phodal/shire/commit/9e71d1e6a7cc774e8714d20adac8920376449704))
* **shirelang:** add support for STRUCTURE command [#170](https://github.com/phodal/shire/issues/170) ([be8084e](https://github.com/phodal/shire/commit/be8084ef487fc403d547d540b0fbc6e2dd24e087))
* **ui:** add element list and append text functionality ([fb5c61d](https://github.com/phodal/shire/commit/fb5c61dca54d2c374836035df2f153d988ece929))
* **ui:** extract LookupManagerListener to separate file [#170](https://github.com/phodal/shire/issues/170) ([6514ce9](https://github.com/phodal/shire/commit/6514ce9fe1b72ce17f892f939917432f7233188d))

## [1.2.3](https://github.com/phodal/shire/compare/v1.2.2...v[1.2.3]) (2024-12-23)

### Bug Fixes

* **core:** handle missing Shire language and FileCreateService ([dcece27](https://github.com/phodal/shire/commit/dcece27bdbd97cbdb05bee4637d4fbe3cc146538))
* fix Compatibility issue for 243 ([81b5d5f](https://github.com/phodal/shire/commit/81b5d5f002c206d349525e0ff241b64ec538c58a))

### Features

* **shire:** add FileCreateService and integrate with ShireInput ([50e698e](https://github.com/phodal/shire/commit/50e698ecd49c057dc5c3d86d870e14e84292e433))
* **ui:** add stop button and refactor document listener ([c544688](https://github.com/phodal/shire/commit/c544688ad3b8a148d26a89959d49954aec70ce2c))

## [1.2.2](https://github.com/phodal/shire/compare/v1.2.1...v[1.2.2]) (2024-12-23)

### Bug Fixes

* **core:** Unregister the shortcut to ensure it works the second time ([5381851](https://github.com/phodal/shire/commit/5381851ed60e406ad9a8ed657a9a067986beb6cd))
* **llm:** NPE in streamSSE ([07a900c](https://github.com/phodal/shire/commit/07a900c4009d5b5eae122f31d9b5577222e87f7f))
* **run:** 修复ShireProgramRunner的ID获取问题并优化代码结构 ([b4aaabe](https://github.com/phodal/shire/commit/b4aaabe49c3bc235ddd471e4e6641bc4acb25d1a))

### Features

* **CodeHighlight:** enhance editor with line number handling [#159](https://github.com/phodal/shire/issues/159) ([4a416f4](https://github.com/phodal/shire/commit/4a416f43203b3ce78a22a4ed60ce6c9fe5b7c891))
* **compiler:** add line number functionality [#159](https://github.com/phodal/shire/issues/159) ([471d710](https://github.com/phodal/shire/commit/471d7107d6dfd6b3f7b9f2652488bf35e26d3770))
* **lifycycle:** Add TimingStreamingService and refactor provider interface [#160](https://github.com/phodal/shire/issues/160) ([656915e](https://github.com/phodal/shire/commit/656915e5031368436035cad803fe496cd4999adf))
* **marketplace:** replace LightVirtualFile with ScratchRootType for temp files [#165](https://github.com/phodal/shire/issues/165) ([11c1082](https://github.com/phodal/shire/commit/11c1082117b71f620fa190e29efb1d1615abfd48))
* **run:** add ShireConsoleView and ShireProcessAdapter ([617ae40](https://github.com/phodal/shire/commit/617ae40dcc0be7d584bf835ab5527b831a9cfd1f))
* **runner:** add ShireFileRunService and enhance run configuration ([e08c831](https://github.com/phodal/shire/commit/e08c83140c2f40185cd36b5f16ef3d99fdc462ca)), closes [#165](https://github.com/phodal/shire/issues/165)
* **shire:** add chatbox functionality and update file references ([d045cd3](https://github.com/phodal/shire/commit/d045cd319108d2361a3ab20631a13091e6a5b6ca))
* **streaming:** add logging streaming service and refactor streaming API [#160](https://github.com/phodal/shire/issues/160) ([ecbd342](https://github.com/phodal/shire/commit/ecbd342ab3da934c9f2b12d01f423a171c4a2505))
* **streaming:** Add profiling capability and enhance documentation [#160](https://github.com/phodal/shire/issues/160) ([8f299e2](https://github.com/phodal/shire/commit/8f299e2a5bd1f185632648883b43aaffd4d5da86))
* **streaming:** add StreamingServiceProvider interface and OnStreamingService [#160](https://github.com/phodal/shire/issues/160) ([8997e96](https://github.com/phodal/shire/commit/8997e966299f8b181bbaeab008894e30a66f76d1))
* **streaming:** enhance OnStreamingService with registration and notification [#160](https://github.com/phodal/shire/issues/160) ([b7cc22e](https://github.com/phodal/shire/commit/b7cc22e539c55a21ef808e59b70b75e7b449233b))
* **ui:** add custom progress bar and input section components [#165](https://github.com/phodal/shire/issues/165) ([6531f4c](https://github.com/phodal/shire/commit/6531f4c882e54ca9235a9308c2651b934894885a))
* **ui:** add scratch file management to ShireInput ([510965e](https://github.com/phodal/shire/commit/510965eda304107ab0d87a588da76b88d139784c))
* **ui:** add ShireInputTextField component and integrate with ShireInputSection [#165](https://github.com/phodal/shire/issues/165) ([7f71c0d](https://github.com/phodal/shire/commit/7f71c0d55d698a646b195f9e9466af99aa1751f4))
* **utils:** add PostCodeProcessor for markdown code formatting ([11cd109](https://github.com/phodal/shire/commit/11cd109ba816f0210a3582e68cd7b9608ae06f6e))
* **variable-resolver:** add support for selection with line numbers [#159](https://github.com/phodal/shire/issues/159) ([cdf84c4](https://github.com/phodal/shire/commit/cdf84c4262dc2928de8e57de50b0b91f65fc62cf))
* **viewer:** Add cancel functionality to progress bar ([d947425](https://github.com/phodal/shire/commit/d9474257b9a5cf8b1846ca4ebdce66eb591b2cf5))

## [1.2.1](https://github.com/phodal/shire/compare/v1.2.0...v[1.2.1]) (2024-12-12)

### Bug Fixes

* **diff:** correct line selection logic in DiffStreamHandler [#153](https://github.com/phodal/shire/issues/153) ([511f7f1](https://github.com/phodal/shire/commit/511f7f1d63591cd1b74684fcb78ca6ffb665ef00))
* **diff:** optimize StreamDiff handling and add DiffLineType conversion [#153](https://github.com/phodal/shire/issues/153) ([b103f58](https://github.com/phodal/shire/commit/b103f58bf282c6da8b13639ba2d413312dcbafe4))
* **openrewrite:** resolve missing OpenRewriteType class ([46f4084](https://github.com/phodal/shire/commit/46f408436fc31f53e9307446b61d5a46a8ca1dee))
* **ui:** remove redundant revalidate and repaint calls ([78374cd](https://github.com/phodal/shire/commit/78374cdbf6ebdb6a9e2f3177be06e9cb50ab0605))
* **utils:** prevent adding empty new lines to CodeFence content ([449efdf](https://github.com/phodal/shire/commit/449efdf5b35972c8bdd04b4078a2e8f4dec297d0)), closes [#153](https://github.com/phodal/shire/issues/153)

### Features

* **action:** Add RunCode action and refactor execution flow ([11d48c0](https://github.com/phodal/shire/commit/11d48c089cef5c4aeba7b27144de9fd5e9fa6c1c))
* **code-highlight:** enhance setupActionBar and add Mermaid support [#153](https://github.com/phodal/shire/issues/153) ([f8f69a2](https://github.com/phodal/shire/commit/f8f69a26fb10ceef4810db9e431ab1bbf3f09432))
* **core:** add DiffLangSketchProvider for diff language support [#153](https://github.com/phodal/shire/issues/153) ([61b592b](https://github.com/phodal/shire/commit/61b592bfeb0cab4b4dd59132c6bd0f12d14c1d72))
* **diff-ui:** implement apply patch from clipboard dialog and enhance SingleFileDiffLangSketch [#153](https://github.com/phodal/shire/issues/153) ([a1e89d2](https://github.com/phodal/shire/commit/a1e89d29fde66eacf29b538992ed3a353dbdfb97))
* **diff:** add diff streaming support for editors [#153](https://github.com/phodal/shire/issues/153) ([f2014bc](https://github.com/phodal/shire/commit/f2014bc60fc29f77f5472bb6fe87d1bc70582fc0))
* **diff:** enhance diff view and context handling ([ea610a4](https://github.com/phodal/shire/commit/ea610a4e3febb39a9dd76376e8f6ce5e77027e4a))
* **diff:** enhance SingleFileDiffLangSketch with new actions and dialog ([72a667f](https://github.com/phodal/shire/commit/72a667f856bf8d62d3e67ac9b7fe03fbcbd33545)), closes [#153](https://github.com/phodal/shire/issues/153)
* **DiffPatchViewer:** enhance patch application and UI [#153](https://github.com/phodal/shire/issues/153) ([a2099cf](https://github.com/phodal/shire/commit/a2099cfc3da4e472332bdddd3c7abe272b223da8))
* **diffs:** add StreamDiff feature and improve UI [#153](https://github.com/phodal/shire/issues/153) ([b260b49](https://github.com/phodal/shire/commit/b260b497adfd9f36b73dcd337448a0342dadbf79))
* **extensions:** add DiffStreamService project service [#153](https://github.com/phodal/shire/issues/153) ([d8cb1b0](https://github.com/phodal/shire/commit/d8cb1b055507d9062137dbe386e192504ae78b07))
* **patch:** add clipboard patch application dialog ([7ea22c5](https://github.com/phodal/shire/commit/7ea22c542d895e75c94bf84c329455a67860b4b1))
* **shirelang:** implement `/goto` command for file navigation [#153](https://github.com/phodal/shire/issues/153) ([bd37fda](https://github.com/phodal/shire/commit/bd37fda786e3e6f1b6e131a8dc51b9c1eba95094))
* **stream-diff:** implement stream diff functionality [#153](https://github.com/phodal/shire/issues/153) ([57ee622](https://github.com/phodal/shire/commit/57ee622fd0c470e561e733d17a55a6ced481e0a0))
* **toolbar:** add ShireDiffCodeAction [#153](https://github.com/phodal/shire/issues/153) ([74d995a](https://github.com/phodal/shire/commit/74d995a13caca4580a3ed95ff47785f578d5dabe))
* **toolsets:** add mermaid support and integration plugin [#153](https://github.com/phodal/shire/issues/153) ([55e33d0](https://github.com/phodal/shire/commit/55e33d0b34a8644c7f6a92990dba06ea0a044b7f))
* **ui:** add diff viewer and toolbar actions [#153](https://github.com/phodal/shire/issues/153) ([ed903cd](https://github.com/phodal/shire/commit/ed903cd1bb8d05ca86361291b0977d470db796d4))
* **ui:** add doneUpdateText method and optimize component handling [#153](https://github.com/phodal/shire/issues/153) ([5622123](https://github.com/phodal/shire/commit/56221238217a4fddfa655f0c9358eea3b5c1f55a))
* **ui:** add patch display and action buttons to ShireMarketplaceTableView [#153](https://github.com/phodal/shire/issues/153) ([17eb9ff](https://github.com/phodal/shire/commit/17eb9ffbdd7960fb5d29b97a80f29d352127dbf8))
* **ui:** enhance diff viewer with preview dialog [#153](https://github.com/phodal/shire/issues/153) ([b9a62db](https://github.com/phodal/shire/commit/b9a62db07c2dde01b228015d478ecbc3538b6c91))
* **ui:** enhance DiffPatchViewer with project context and diff viewing [#153](https://github.com/phodal/shire/issues/153) ([7b88f2a](https://github.com/phodal/shire/commit/7b88f2a1efb2b491d4a39d8c8954e846b9b09bcc))
* **ui:** enhance DiffPatchViewer with project context and diff viewing [#153](https://github.com/phodal/shire/issues/153) ([7cd80ea](https://github.com/phodal/shire/commit/7cd80ea8d5971aa0d514e93bef0e487241ec4e71))
* **ui:** implement LangSketch and add PlantUML support [#153](https://github.com/phodal/shire/issues/153) ([6aa5b75](https://github.com/phodal/shire/commit/6aa5b755d9eed391597a3a02cc2e83225ee115c7))
* **view:** Implement ShirePanelView for enhanced UI representation ([676df38](https://github.com/phodal/shire/commit/676df3895f62522e33a51c99360a6f4123ac3e82))

### Reverts

* revert change of delete file for MKT place ([1adbd5a](https://github.com/phodal/shire/commit/1adbd5a0cc12adb3bae2b4cb8dabbd00a7886fb7))

## [1.1.1](https://github.com/phodal/shire/compare/v1.1.0...v[1.1.1]) (2024-12-02)

### Bug Fixes

* **core:** correctly assign stdout and stderr in RunServiceTask ([c320749](https://github.com/phodal/shire/commit/c32074984d9e757d95625a9103ef16ca46fe4795))
* **core:** resolve compatibility issues with editor highlighter service ([175e6c9](https://github.com/phodal/shire/commit/175e6c96b86bd7cd5ef54ce13583dcaa5b15ad63))
* set default python execute to python 3 and update && closed [#146](https://github.com/phodal/shire/issues/146) ([3f57680](https://github.com/phodal/shire/commit/3f57680f93fd8ee35b7352fa0470f94b457f4349))
* **shire-javascript:** remove deprecated JSXHarmonyFileType reference ([9760487](https://github.com/phodal/shire/commit/9760487a3ead0449dd6297acf1a45edfdeb4d36d))

### Features

* **intellij-plugin:** Add soft wrap and scroll to end actions to console toolbar ([8453f57](https://github.com/phodal/shire/commit/8453f57a38874750a504df56f025f4710a3ae683))
* **shell:** improve temp file cleanup in shell command execution ([8a5493d](https://github.com/phodal/shire/commit/8a5493d2320e502775b256e680859bcc39869516))
* **ui:** add CodeView component for code preview ([bc8e07a](https://github.com/phodal/shire/commit/bc8e07a063d42cd26d0f10b52b7726e4842ae437))
* **ui:** add toolwindow actions for copy, insert code, and language label ([b42962c](https://github.com/phodal/shire/commit/b42962c4e9de0bcfab63a76705935b1340a6444b))
* **ui:** scroll to end of text on insert and add interaction type check ([4b3ecf8](https://github.com/phodal/shire/commit/4b3ecf8f88ba89ea5b777b35b65644e0131c7500))
* **variables:** add Component data class and ReactPsiUtil utility ([0300680](https://github.com/phodal/shire/commit/03006808a73caf24d34feb9c9c4686027889d6ed))

## [1.0.6](https://github.com/phodal/shire/compare/v1.0.4-SNAPSHOT...v[1.0.6]) (2024-11-24)

### Bug Fixes

* **core:** return complexity count as String instead of Int ([4cea791](https://github.com/phodal/shire/commit/4cea7917ed2bfc9553ed77c918b26df9a7efa0a6))
* **runner:** Run fails due to duplicate variables ([430e57a](https://github.com/phodal/shire/commit/430e57a7b375c3709beeb6af6be8b7dbcf0dbe4d))

### Features

* **actions:** add ShireConsoleAction and update related configurations ([8ee1a18](https://github.com/phodal/shire/commit/8ee1a18bb43d0762549064b37377c1e9708052ad))
* **actions:** add ShireDatabaseAction to Database panel menu bar ([19d02df](https://github.com/phodal/shire/commit/19d02dfeefce3b4e6d8fa630a6207a8adfae7c77))
* **compiler:** Improve the logic of the if expression to handle variables in velocityBlock ([61e3aab](https://github.com/phodal/shire/commit/61e3aab3f0e408d883f4cd90e947e31d751ac0ec))
* **kotlin:** add complexity analysis for Kotlin language ([412a90d](https://github.com/phodal/shire/commit/412a90d55a2144162a9d49aa3cfb28b5d0d55253)), closes [#139](https://github.com/phodal/shire/issues/139)
* **provider:** add change count, line count, and complexity count variables [#143](https://github.com/phodal/shire/issues/143) and closed [#139](https://github.com/phodal/shire/issues/139) ([e23687c](https://github.com/phodal/shire/commit/e23687c8a0c31f4b23ea09483e482dd9b3fb53b0))
* **provider:** add process method to ComplexityProvider interface [#139](https://github.com/phodal/shire/issues/139) ([61a6cad](https://github.com/phodal/shire/commit/61a6cad3a26d1c6f884fe1059d6da98ba93d2f15))
* **provider:** implement complexity count calculation for PsiVariableProvider [#139](https://github.com/phodal/shire/issues/139) ([8a8f1e8](https://github.com/phodal/shire/commit/8a8f1e8df6171714ab0f6660fd91474b3fb7b769))
* **shirelang:** add Shire Vcs Log Action and update menu text ([b90f450](https://github.com/phodal/shire/commit/b90f45013defc16685bbe5483af2a71c6c798c0e))
* **shirelang:** add SonarLint action and tool window integration ([20e7f13](https://github.com/phodal/shire/commit/20e7f1308220d4f5ad24f34614ff8783aac0d9ad))
* **shirelang:** add Vcs.Log action to ShireActionStartupActivity ([d34a8da](https://github.com/phodal/shire/commit/d34a8da8f1b2eefef80c3789f52e64af93f5b16c))
* **vcs:** add support for Diff variable in VcsToolchainVariable ([d448e33](https://github.com/phodal/shire/commit/d448e33ee43fd34fb12f7653d84e4421de8f32d7))

## [1.0.4-SNAPSHOT](https://github.com/phodal/shire/compare/v1.0.2...v[1.0.4-SNAPSHOT]) (2024-11-13)

### Features

* **core:** add ActionableWebView class for enhanced web interaction [#132](https://github.com/phodal/shire/issues/132) ([b65fdc0](https://github.com/phodal/shire/commit/b65fdc02d34cc1065bfcc614fd05993822ac2a39))
* **grammar:** Add support for if expressions ([d536289](https://github.com/phodal/shire/commit/d536289b7518fbef89f6c41dc08cb3ee4eab8be4))

## [1.0.2](https://github.com/phodal/shire/compare/v1.0.1...v[1.0.2]) (2024-11-03)

### Bug Fixes

* **hobbit:** refine key bindings for approval processor ([d402baa](https://github.com/phodal/shire/commit/d402baa9192e7f16a7bb3b45a3a0cc1c38be5bd4))
* **openrewrite:** improve run configuration setup [#119](https://github.com/phodal/shire/issues/119) ([eecd294](https://github.com/phodal/shire/commit/eecd294277565db875ec21c8f756b5f1d80219e2))
* **openrewrite:** update working directory for configuration [#119](https://github.com/phodal/shire/issues/119) ([35600e3](https://github.com/phodal/shire/commit/35600e368289e06bb52f68a3db4fbac0b8987913))

### Features

* **brace-matching:** enhance ShireBraceMatcher with improved pairs and conditions ([65f500e](https://github.com/phodal/shire/commit/65f500e1d1cdf72b71bf6235edd55691f1664d84))
* **navigation:** add APPROVAL_EXECUTE pattern and refactor goto declaration handling ([f856236](https://github.com/phodal/shire/commit/f856236edf01eda761d4de1ef396a976e3cbbc18))
* **openwrite:** try to add OpenRewrite support and integration [#119](https://github.com/phodal/shire/issues/119) ([b1cc797](https://github.com/phodal/shire/commit/b1cc7978be1a0fbec4e03ae340f18f1431b6acc0))
* **shirelang:** add formatter support for Shire language files ([a37bf9d](https://github.com/phodal/shire/commit/a37bf9dad6ec86f3a11a5c7c7b683f306e87feaa))
* **shirelang:** add highlight error filter for improved syntax highlighting ([c743528](https://github.com/phodal/shire/commit/c743528ddd01b1900fd63e52985f4e533ec533a6))
* **shirelang:** implement beforeCharTyped override for custom handling ([ee12c7f](https://github.com/phodal/shire/commit/ee12c7f9cd1eb6dda9df4ec5790f1182e3841b89))
* **shirelang:** implement highlighting, brace matching, and quote handling ([ced541f](https://github.com/phodal/shire/commit/ced541fa5bba0b55a8d532984928546afbddbd9a))

## [0.9.1](https://github.com/phodal/shire/compare/v0.9.0...v[0.9.1]) (2024-10-10)

### Bug Fixes

* **actions:** The shire file downloaded from the marketplace has not been dynamically updated ([4a0aa6f](https://github.com/phodal/shire/commit/4a0aa6f57306d693cf78a7098e826f87de1179cc))
* **build:** comment out failureLevel in pluginVerification [#100](https://github.com/phodal/shire/issues/100) ([e260a10](https://github.com/phodal/shire/commit/e260a1055c6b6a93acd0fa938e5e06c400aa0843))
* fix for json module outside project issue [#112](https://github.com/phodal/shire/issues/112) ([ed12724](https://github.com/phodal/shire/commit/ed12724e2fc460e2a511a9becfa0b8f402ed3fb6))
* fix plugin group error issue ([acbdeca](https://github.com/phodal/shire/commit/acbdeca7cd2bc22953cea47eaac37b7c44e06506))
* **github-actions:** correct gradle task paths in release workflow [#100](https://github.com/phodal/shire/issues/100) ([4d5a39d](https://github.com/phodal/shire/commit/4d5a39dbe22ea76845a34ccbadf61c994045643f))
* **javascript:** fix for siganture issue ([94ebee5](https://github.com/phodal/shire/commit/94ebee5af25aa07864175a351780595ffc08df34))
* **javascript:** update variable resolution in JSPsiContextVariableProvider ([8e2a7e9](https://github.com/phodal/shire/commit/8e2a7e91b2344bef5ab9f57aae432f72fdd833ac))
* **lexer:** make sure front-matter parse success [#112](https://github.com/phodal/shire/issues/112) ([a9c3770](https://github.com/phodal/shire/commit/a9c3770fdcf0d52ad412e3a1a157de83e36f13be))
* **logging:** improve error messages for PatternActionFunc arguments ([844dcd8](https://github.com/phodal/shire/commit/844dcd813d069be62d7f6cddb057f1e2d4ef0a58))
* **settings:** Test connection failed without applying settings ([e883e38](https://github.com/phodal/shire/commit/e883e383a540368f26295ce3504d5253f637b294))
* **shirelang:** correct typo in ShireFileModificationListener class name ([8baad36](https://github.com/phodal/shire/commit/8baad367cae909fa19ce542f64041c1be05e65b7))

### Features

* **build.gradle.kts:** import Changelog and update PatchPluginXmlTask ([cd7df2c](https://github.com/phodal/shire/commit/cd7df2c4ea3769aec3681e5418a683015558b37b))
* **build:** configure specific subprojects in build.gradle.kts [#100](https://github.com/phodal/shire/issues/100) ([e7db461](https://github.com/phodal/shire/commit/e7db4611e96e523bc774040857c40d3b04345f71))
* **build:** optimize GitHub Actions build space ([0f3a6f9](https://github.com/phodal/shire/commit/0f3a6f9f6c991619c3fa3be990d0ea0f5211b820))
* **build:** try to resize verify range for reduce size of disk in GitHub Action [#100](https://github.com/phodal/shire/issues/100) ([e127f88](https://github.com/phodal/shire/commit/e127f88b628448dea3d80cbf3befa951c9011e73))
* **build:** update Gradle setup and plugin verification tasks ([b779cd0](https://github.com/phodal/shire/commit/b779cd0be31599f8e05e45ae634fdec0689fbe04))
* **build:** update Gradle version and enable PatchPluginXmlTask [#100](https://github.com/phodal/shire/issues/100) ([8044084](https://github.com/phodal/shire/commit/804408437ab7b3e6fb9c6e6d15d7dc566b4a4e90))
* **compiler:** enable support for custom foreign functions [#116](https://github.com/phodal/shire/issues/116) ([c4e51fc](https://github.com/phodal/shire/commit/c4e51fcc4a64356910723673c2a3c52cdd51b57f))
* **compiler:** implement foreign function support [#116](https://github.com/phodal/shire/issues/116) ([61059cd](https://github.com/phodal/shire/commit/61059cd8982c6383b6a09584d5caf78fd4769724))
* **compiler:** refine input type rules in PatternActionProcessor [#112](https://github.com/phodal/shire/issues/112) ([db58a87](https://github.com/phodal/shire/commit/db58a874035ef8c5a202e22f85959e146093f091))
* **docs, shirelang:** update function parameters and syntax highlighting [#116](https://github.com/phodal/shire/issues/116) ([b6c6e9d](https://github.com/phodal/shire/commit/b6c6e9d98f58c11cedb0f1164f86dd43be777a52))
* **gradle:** update IntelliJ IDEA version and add plugin verification [#100](https://github.com/phodal/shire/issues/100) ([c5b58a1](https://github.com/phodal/shire/commit/c5b58a1d4d9fbb96788b72634ca216caa10b8448))
* **gradle:** update IntelliJ platform version and add shire-json language support [#100](https://github.com/phodal/shire/issues/100) ([da4f120](https://github.com/phodal/shire/commit/da4f1206e70922bfd10960981123e9d4d1c8771b))
* **IDEA-243:** add deps plugins [#100](https://github.com/phodal/shire/issues/100) ([276f391](https://github.com/phodal/shire/commit/276f391c617265d67e562616787322db3c3fa123))
* init tokenizer function for [#104](https://github.com/phodal/shire/issues/104) ([1ae89ed](https://github.com/phodal/shire/commit/1ae89ed7c39a4f4421f6946fb24e14d232596f59))
* **javascript:** enhance JavaScriptLanguageToolchainProvider for detailed context collection ([d4341e4](https://github.com/phodal/shire/commit/d4341e44abd263c0c932ff86e729c26c9aa1c747))
* **javascript:** fix for import issues ([c646e4c](https://github.com/phodal/shire/commit/c646e4c568803e3e7b34ed3dff4904a059e3c0b7))
* **json-plugin:** add dependency on IntelliJ JSON plugin [#100](https://github.com/phodal/shire/issues/100) ([ce0bbad](https://github.com/phodal/shire/commit/ce0bbadaebd310307f29409c324803ad2d8e9a7d))
* **markdown:** add support for markdown headers in ShireCompileTest [#112](https://github.com/phodal/shire/issues/112) ([b1d96f6](https://github.com/phodal/shire/commit/b1d96f6c18568766c1213911944a7a41929ccd2f))
* **parser:** add process and output for parser [#116](https://github.com/phodal/shire/issues/116) ([5b9bbd9](https://github.com/phodal/shire/commit/5b9bbd995ee4f10259eb3198458e6e74843c9a50))
* **parser:** Support Markdown headers in ShireParser [#112](https://github.com/phodal/shire/issues/112) ([f3e2e09](https://github.com/phodal/shire/commit/f3e2e0936e8a2dc9cdddf5e211f4065ec359d33f))
* **patternaction:** add tokenizer function and refactor PatternActionFuncType to PatternActionFuncDef ([37ece32](https://github.com/phodal/shire/commit/37ece322421625f46b90ed96615bced0792ca36e))
* **patternaction:** enhance function documentation and formatting [#112](https://github.com/phodal/shire/issues/112) ([1dfd2a9](https://github.com/phodal/shire/commit/1dfd2a9d553e8ff905249c1ab994bb3a8b57c080))
* **plugin:** update plugin name and improve markdown table formatting ([05aa38a](https://github.com/phodal/shire/commit/05aa38a5a5440514429c08da1fcb1c878d7be2a3))
* **post-processors:** Add descriptions and list view for processors [#112](https://github.com/phodal/shire/issues/112) ([4535863](https://github.com/phodal/shire/commit/4535863463b322b2c9619432d4180250c9e43f3b))
* **shire-json:** move JSON related functionalities from core to shire-json module [#100](https://github.com/phodal/shire/issues/100) ([3f3a77a](https://github.com/phodal/shire/commit/3f3a77a4399a6e66c7b400d452cb1509f2fb674d))
* **ShireFileListener:** ignore directories and LightVirtualFiles in onUpdated method ([086ed79](https://github.com/phodal/shire/commit/086ed79e9064c56d4eef15b2dee41110fa886c2f))
* **shirelang:** add example option to ShireToolchainFunctionProvider [#112](https://github.com/phodal/shire/issues/112) ([4c16e36](https://github.com/phodal/shire/commit/4c16e3668540b95a544e00c55583e83f66602645))
* **shirelang:** add ShireToolchainFunctionProvider and update GitFunctionProvider [#112](https://github.com/phodal/shire/issues/112) ([90a962f](https://github.com/phodal/shire/commit/90a962f762f81537416aba7abe310e72864193dc))
* **shirelang:** add support for custom functions and update lexer and parser [#116](https://github.com/phodal/shire/issues/116) ([14ef5a0](https://github.com/phodal/shire/commit/14ef5a0ee6e4c30355d8833233b098cc666c475c))
* **shirelang:** enhance foreign function execution with args support ([f9658ea](https://github.com/phodal/shire/commit/f9658ea65d4a7453985c7327d6f43422c4c227f4)), closes [#119](https://github.com/phodal/shire/issues/119)
* timeout ([c02f7b2](https://github.com/phodal/shire/commit/c02f7b29ef0f0389b3a9e5adac72ff7a6415bfb5))
* **tokenizer:** add Jieba Chinese tokenizer support [#112](https://github.com/phodal/shire/issues/112) ([2f73edf](https://github.com/phodal/shire/commit/2f73edf38b6ce0124d37ab99f26de8a931d0098c))
* **tokenizer:** add multiple tokenizer types support in TokenizerProcessor [#100](https://github.com/phodal/shire/issues/100) ([9575b14](https://github.com/phodal/shire/commit/9575b141109b81ad87157bbe2546908960e53868))
* **tokenizer:** add variable substitution in tokenizer and related test [#104](https://github.com/phodal/shire/issues/104) ([30c40c5](https://github.com/phodal/shire/commit/30c40c52b1cef465d49339aeb3b9c1b59764cb45))
* **tokenizer:** ensure distinct tokenization results ([09746ca](https://github.com/phodal/shire/commit/09746cafce7766ffe2a8a6d8b9f866def8a98d21))

## [0.8.2](https://github.com/phodal/shire/compare/v0.8.1...v[0.8.2]) (2024-09-23)

### Bug Fixes

* **middleware:** use <html to replace <html> ([483ab87](https://github.com/phodal/shire/commit/483ab8781e146ba483906394d285c02a217db1bb))

### Features

* add OpenWebpage processor ([24c2bf2](https://github.com/phodal/shire/commit/24c2bf217e601f99bd4628e9438f3edbc0e5d14a))
* **core:** add ShowWebviewProcessor and WebViewWindow ([6a3ddb4](https://github.com/phodal/shire/commit/6a3ddb47110621558fdc46feaed2e27246bcd116))
* **core:** allow OpenFileProcessor to accept arguments ([f54e0c0](https://github.com/phodal/shire/commit/f54e0c0ed14292018ae9c99b894cb95299019ea8))
* **core:** enhance error message in RunCodeProcessor ([87d65cd](https://github.com/phodal/shire/commit/87d65cdc3868efffc3ee91277a3aff07590df85e))
* **database:** add Excel toolchain functions ([868510c](https://github.com/phodal/shire/commit/868510c6cf949efc813d2e0d7dfd64ed1ae1e3e2))
* **docs:** update documentation and add WebView functionality ([15b5a4c](https://github.com/phodal/shire/commit/15b5a4c280c91173cbc34d33e2ebffc02155ef64))
* **gradle:** update pluginUntilBuild version in gradle.properties ([a219ce4](https://github.com/phodal/shire/commit/a219ce41edd44aee7f44c261b27967a3eb0c227a))
* **httpclient:** add log display in console for HTTP requests ([d181831](https://github.com/phodal/shire/commit/d18183196a3e77845db1dd538d350078b7f4f54e))
* **httpclient:** improve request logging format in CUrlHttpHandler ([3f7740e](https://github.com/phodal/shire/commit/3f7740e7ecdf0e30935f605c16e86ea743ec7c1f))
* **httpclient:** refactor CUrlHttpHandler and CUrlConverter for better variable handling ([a039679](https://github.com/phodal/shire/commit/a03967985a5156c8abfc56bb96a3b699495b6e7e))
* **json-path:** enable for parse start for `data:` ([d5a2d71](https://github.com/phodal/shire/commit/d5a2d71b8eba81c7ecd42e787870552cd59d9d54))
* **PatternFuncProcessor:** add newline join for Array and List results ([57ea7e6](https://github.com/phodal/shire/commit/57ea7e6e609173f6c67bcfd32eafa71cb17710aa))
* **ShireFileModifier:** wrap file processing in runReadAction ([e8cc6f0](https://github.com/phodal/shire/commit/e8cc6f06fd8238ea7679dc0e31a1c3f976d2f1d0))
* **UI:** enable off-screen rendering and improve webview popup ([96f3791](https://github.com/phodal/shire/commit/96f37913f889de070c608313481361cb73badce4))

## [0.8.1](https://github.com/phodal/shire/compare/v0.8.0...v[0.8.1]) (2024-09-18)

### Bug Fixes

* **core:** update file path validation regex and related tests ([3ab4e9f](https://github.com/phodal/shire/commit/3ab4e9f4e8d42643170141ecc4d0538612907def))
* **database:** update rror messages ([8b4e932](https://github.com/phodal/shire/commit/8b4e932e12cef7a6a2df78b8c76303ceaff5c15a))
* **llm:** add configuration update from state in OpenAILikeProvider [#85](https://github.com/phodal/shire/issues/85) ([56a1961](https://github.com/phodal/shire/commit/56a19612a4e4fb94471d4a63b876d7b1b6b7d562))
* **middleware:** 修正文件保存处理器，避免非法文件名 ([35785bd](https://github.com/phodal/shire/commit/35785bdb980f28ba61d30c7827a29d5f33658275))
* **patternaction:** 修复PatternActionFunc中的空指针异常 ([e048ab9](https://github.com/phodal/shire/commit/e048ab9c058393ad6a9aa509ceeda52c896357ea))
* **PatternFuncProcessor:** enhance path resolution and refactor tests [#83](https://github.com/phodal/shire/issues/83) ([a81c603](https://github.com/phodal/shire/commit/a81c6032b36f295c2c828010546bae61d64d600f))
* **PatternFuncProcessor:** remove joinToString from array operations [#83](https://github.com/phodal/shire/issues/83) ([9e251f7](https://github.com/phodal/shire/commit/9e251f7734affa2cc24c1d8be59be97a5bd48f60))
* **runner:** adjust execution subscription order in ConfigurationRunner ([8ba7447](https://github.com/phodal/shire/commit/8ba7447e5e5570281d370a38917a089cdd67c5d3))
* **runner:** LlmProvider is still working after canceling the shire process ([d458e1f](https://github.com/phodal/shire/commit/d458e1f88e0ea621b77e593b6b1dc66aba7e7974))
* **runner:** 简化输出LLM模型名 ([06ddd76](https://github.com/phodal/shire/commit/06ddd76fca69ca6782ed3bc09ed4d2e196c27295))
* **shirelang:** add empty intentions check and change error level in ShireIntentionHelper ([badc42c](https://github.com/phodal/shire/commit/badc42cb9e6f5dc09326ee0ffdb773fa4a76fe15))
* **shirelang:** refactor array handling and add regression test [#83](https://github.com/phodal/shire/issues/83) ([1f4c8fe](https://github.com/phodal/shire/commit/1f4c8fe8d96e65959f3c0d61cb6899febb87edf6))
* **Wiremock:** 添加文件路径到 Wiremock 错误信息中，以便于调试 ([2a888cd](https://github.com/phodal/shire/commit/2a888cd75703c161bb009e0569276557862eae5e))
* 在ShireProcessHandler中添加异常日志记录 ([28d6dea](https://github.com/phodal/shire/commit/28d6dea0c4650b6254079ed2fee5af6b78ccfe70))

### Features

* add basic batch processor of content ([ec4fa1c](https://github.com/phodal/shire/commit/ec4fa1c58d2053fd22bfdea8c87a318473879d96))
* add more psiUtil for helper [#89](https://github.com/phodal/shire/issues/89) ([b4fa3eb](https://github.com/phodal/shire/commit/b4fa3eb59c6ab2b64430392bbf4aa5090f283796))
* **batch-processing:** implement batch processing functionality ([f38dbc2](https://github.com/phodal/shire/commit/f38dbc2915891945fae99f245e2d3425142bd5cc))
* **batch:** add custom variables support to ShireTemplateCompiler ([6ce8430](https://github.com/phodal/shire/commit/6ce8430ca9a35e272824f43e9ad2b159b7acb9e9))
* **batch:** add goto decl ([9412afb](https://github.com/phodal/shire/commit/9412afbdf5ff6c38999c603bcd12d4a0055442be))
* **beforeStreaming:** refactor function naming and add coroutine support ([1a1bd30](https://github.com/phodal/shire/commit/1a1bd30964bf477dcdd038211f2b4b3e13625fc1))
* **codemodel:** enhance class name extraction in DirectoryStructure [#89](https://github.com/phodal/shire/issues/89) ([dfb02b3](https://github.com/phodal/shire/commit/dfb02b320cfc3fe28528fb513053b47cd2b555f4))
* **compiler:** add Batch and Destroy functions to PatternActionFunc ([e32fc61](https://github.com/phodal/shire/commit/e32fc61cebd44597c09beba059c32e904c5592fe))
* **compiler:** refactor action classes and add beforeStreaming functionality ([461361d](https://github.com/phodal/shire/commit/461361df471d668cf4d6748cf53d150306501319))
* **execute:** enable for gradle run support ([9521375](https://github.com/phodal/shire/commit/95213753eaeaf10ab4da3f17e72a4e0d92deec7d))
* **git:** add git commit function support ([820ec89](https://github.com/phodal/shire/commit/820ec89991e313caf5d9aa77a7039114c3a4e726))
* **git:** enhance commitChanges function in GitFunctionProvider ([fb7c286](https://github.com/phodal/shire/commit/fb7c286f31bee87565ac83388ac932becfee7daa))
* **go:** add golang tool context provider [#89](https://github.com/phodal/shire/issues/89) ([6058a5a](https://github.com/phodal/shire/commit/6058a5a680a2bb5886a810884e90beb6f47ef921))
* **GoLanguageProvider:** add method to get Go version [#89](https://github.com/phodal/shire/issues/89) ([56478b5](https://github.com/phodal/shire/commit/56478b52b0d367acef7bb984cf3db3f743b3ae75))
* **GoPsiContextVariableProvider:** map related types to text [#89](https://github.com/phodal/shire/issues/89) ([cce91ed](https://github.com/phodal/shire/commit/cce91ed81c4be2b9a80412e8fa9d8bac10b5f28e))
* init downloader for marketplace [#86](https://github.com/phodal/shire/issues/86) ([9aaa430](https://github.com/phodal/shire/commit/9aaa43010dca2b55655e32d3ae46961851367658))
* **javascript:** add variable provider and utility functions ([1e5ff4e](https://github.com/phodal/shire/commit/1e5ff4e2a7084720c8a9ba3b40a841b418140a81))
* **lifecycle:** add 'beforeStreaming' and 'mock' functions ([293d361](https://github.com/phodal/shire/commit/293d3616296451a8e892a9bb41fe082a8c10cdef))
* **marketplace:** add download notifications and refresh functionality [#86](https://github.com/phodal/shire/issues/86) ([97482aa](https://github.com/phodal/shire/commit/97482aa964ee633224ca58e55bea44b66178dc4b))
* **marketplace:** add MarketplacePanel UI and functionality [#86](https://github.com/phodal/shire/issues/86) ([ece56e0](https://github.com/phodal/shire/commit/ece56e07f267f4ea1f7fe85c9165549e21621ab7))
* **marketplace:** add MarketplaceToolWindowFactory and ShireIdeaIcons [#86](https://github.com/phodal/shire/issues/86) ([70ef324](https://github.com/phodal/shire/commit/70ef324d15432ab190d25b8ebe8304f65281c5c4))
* **marketplace:** add refresh button and refactor UI layout [#86](https://github.com/phodal/shire/issues/86) ([f996913](https://github.com/phodal/shire/commit/f99691388694d64c8ecac7e652b3f1f18c50af63))
* **marketplace:** change UI anchor and update error messages [#86](https://github.com/phodal/shire/issues/86) ([a34fd1c](https://github.com/phodal/shire/commit/a34fd1c1cf7c9ccc56a981a49d5c7e9ef7503f8d))
* **marketplace:** enhance ShirePackage table with download functionality [#86](https://github.com/phodal/shire/issues/86) ([e868010](https://github.com/phodal/shire/commit/e868010c94e958df6b465e28b12a027212cef31a))
* **marketplace:** refactor MarketplacePanel and add table view [#86](https://github.com/phodal/shire/issues/86) ([7088492](https://github.com/phodal/shire/commit/70884927f878b2ca193c45e5781a9ea86a7e894a))
* **marketplace:** refactor table component and add install action [#86](https://github.com/phodal/shire/issues/86) ([fc18040](https://github.com/phodal/shire/commit/fc18040664e54dce91189b57c81ecad45739e7e7))
* **marketplace:** update ShireMarketplaceTable to fetch data from API [#86](https://github.com/phodal/shire/issues/86) ([bcecadb](https://github.com/phodal/shire/commit/bcecadbe2c19e227b42ee6dc858b1610146fa1c7))
* **mock:** init for run mock serivce ([ec41dfb](https://github.com/phodal/shire/commit/ec41dfbe31dd8dafa2c026a8d26226d20f503369))
* **mock:** init mock server for testing apis ([6813876](https://github.com/phodal/shire/commit/6813876203fdb4c313d07895cae9d63939666846))
* **mock:** update Wiremock provider path and improve error handling ([2389f53](https://github.com/phodal/shire/commit/2389f53b53c7db0a3b47826fae412574637955dd))
* **patternaction:** refactor pattern action function parsing ([4b6e13a](https://github.com/phodal/shire/commit/4b6e13a9f0c5ed7bc5243ba0a6ffef0ff0e89bf7))
* **PatternFuncProcessor:** change argument addition order and improve error message ([9d2c1bc](https://github.com/phodal/shire/commit/9d2c1bc22fcde20caf3ad34ed52dd24ae6dbc797))
* **python:** init py psi util ([c441f62](https://github.com/phodal/shire/commit/c441f626132224d34a375bbcb5fb19e0bb68ca84))
* **run-service:** wrap operations in runReadAction in ShirePythonRunService.kt ([478a3d9](https://github.com/phodal/shire/commit/478a3d990c23cb5d30520cf876fe886c3cd2499f))
* **search:** disable semantic embedding functionality ([3560a23](https://github.com/phodal/shire/commit/3560a23c1481f9aeddc71bbc86c8739094387345))
* **shire-go:** add GoPsiContextVariableProvider for Go language support [#89](https://github.com/phodal/shire/issues/89) ([aefdadb](https://github.com/phodal/shire/commit/aefdadbb28e8fa65799445d796f082b19db91c99))
* **shire-go:** add iota detection in Go expressions and constants [#89](https://github.com/phodal/shire/issues/89) ([33216d7](https://github.com/phodal/shire/commit/33216d77c9e2d4fb510d5f8f8f74ebfeae92e32c))
* **shire-go:** add support for Go language [#89](https://github.com/phodal/shire/issues/89) ([ab09a6f](https://github.com/phodal/shire/commit/ab09a6fe57586d1ac65893346e18373e6bac8af1))
* **shire-go:** enhance related classes and code smell handling in GoPsiContextVariableProvider [#89](https://github.com/phodal/shire/issues/89) ([8d91a88](https://github.com/phodal/shire/commit/8d91a88943e350e5365ceb6705f6ceeee42bf5e2))
* **variable-provider:** enhance context variable handling in JS and Go ([d344944](https://github.com/phodal/shire/commit/d34494437238f58c06c2e4c6c403be02735faf9a))
* 添加 LLM 提供者未找到的错误信息 ([fde5a88](https://github.com/phodal/shire/commit/fde5a88f24ce99392ccc67335bc7f81c25d20e6e))

## [0.7.4](https://github.com/phodal/shire/compare/v0.7.3...v[0.7.4]) (2024-09-09)

### Bug Fixes

* **db:** fix toolchain call issue ([99eb680](https://github.com/phodal/shire/commit/99eb680b20d806967df871b64a0c752e844f6e76))
* **runner:** An unexpected exception occurred, causing the shire process cannot be canceled ([7eba18c](https://github.com/phodal/shire/commit/7eba18c8b1adaccf226fad1362a239cb60d19da9))
* **runner:** The consoleView is not the original consoleView when processing the exit code of the script ([474b681](https://github.com/phodal/shire/commit/474b6813565e790a832c37f74e7ac4acd6db7696))
* **runner:** The messageFilter of the console view appends extra data ([a47db5d](https://github.com/phodal/shire/commit/a47db5dd1fe51408409339a4711c7d16a23922d1))
* **shirelang:** ensure null safety in ShireVcsSingleAction [#78](https://github.com/phodal/shire/issues/78) ([0c4665b](https://github.com/phodal/shire/commit/0c4665b9faf4573b2fb66abb1fabccc484fc3d51))

### Features

* **actions:** add support for enabling/disabling actions and improve action config handling [#78](https://github.com/phodal/shire/issues/78) ([045c962](https://github.com/phodal/shire/commit/045c962be11e55139c83cdb241d3a8a13f749b52))
* **codemodel:** Add JavaScript and TypeScript structure providers ([ce936bd](https://github.com/phodal/shire/commit/ce936bda74be3e1aa07bc47ffde133fefa867f8e))
* **javascript:** add JavaScript support and build system integration ([c09e3d3](https://github.com/phodal/shire/commit/c09e3d36483be5e85c3cead1dbec88f361f16d71))
* **javascript:** add JestCodeModifier and JSAutoTestingService ([fde85ad](https://github.com/phodal/shire/commit/fde85ad1e959a18d102e4ce44c6197e0914f3bc8))
* **javascript:** implement TypeScript refactoring tool and language support ([f46a3f8](https://github.com/phodal/shire/commit/f46a3f877a2aeed6fdce703a9b3008e617cf6625))
* **llm:** add LlmConfig class for LLM configuration management [#78](https://github.com/phodal/shire/issues/78) ([a824eda](https://github.com/phodal/shire/commit/a824eda7e6bc15b7e81c8fc088484582c1bd4276))
* **llm:** add maxTokens parameter to CustomFields and LlmConfig [#78](https://github.com/phodal/shire/issues/78) ([c47ae75](https://github.com/phodal/shire/commit/c47ae75e062a65b7b2ca4479efa16346c05dd644))
* **model:** enable for custom model in project && closed [#78](https://github.com/phodal/shire/issues/78) ([d3e4859](https://github.com/phodal/shire/commit/d3e4859b28f60d7b85e8188ae50eaf5a46ba1abb))
* **navigation:** implement GotoDeclarationHandler for Shire language ([2c7d744](https://github.com/phodal/shire/commit/2c7d7444759e81e9c3661f5ec5633ae7a086f810))
* **pattern-action:** refactor to use PatternActionFuncType enum ([445b2ac](https://github.com/phodal/shire/commit/445b2ac9e9539cf41468a7c9a0a819b98d63aa7f))

## [0.7.2](https://github.com/phodal/shire/compare/v0.7.1...v[0.7.2]) (2024-09-05)

### Bug Fixes

* **runtime:** switch to workerThread for terminal UI tasks execution [#72](https://github.com/phodal/shire/issues/72) ([53672d3](https://github.com/phodal/shire/commit/53672d3def0a373de2358a0754a3798c398e8934))

### Features

* **database:** add function to retrieve and display database info ([c30dd13](https://github.com/phodal/shire/commit/c30dd130809290e1a5495ab5960f098945b02f73))
* **httpclient:** enable pass variable table value to curl.sh file ([615b280](https://github.com/phodal/shire/commit/615b280ec54f4b76dfd1b1823f815373865a6747))
* **middleware:** add DiffProcessor support [#66](https://github.com/phodal/shire/issues/66) ([b3110f6](https://github.com/phodal/shire/commit/b3110f634fe71be5c11170ccfae155558fd714de))
* **middleware:** add Patch processor for applying code patches ([425fdb4](https://github.com/phodal/shire/commit/425fdb4ab28aa6f06ee645339d70289b9ae3acf8))
* **parser:** enable regex pattern function support ([8f88ddd](https://github.com/phodal/shire/commit/8f88ddd0e21d5326f9b6a20c53b43f0c438749c9))
* **parser:** implement custom ShireGrepFuncCall and refactor related components ([4a319ec](https://github.com/phodal/shire/commit/4a319ec9295b9b82e851c8bd1cdbacb822bd240e))
* **parser:** implement sed function call and improve injection handling ([00f3aca](https://github.com/phodal/shire/commit/00f3acaf0724eaeea8ce0535ee15bad3d5016f70))
* **shirelang:** implement regex pattern support for 'grep' function ([e3bb682](https://github.com/phodal/shire/commit/e3bb6828bfa72e9fba2bb39f954097487c8fa1bb))
* **testing:** add Shire language annotation to test cases and implement shell script runner ([9c53db9](https://github.com/phodal/shire/commit/9c53db9d4609cb78c026a93fb6b719c4b294a05f))

## [0.7.1](https://github.com/phodal/shire/compare/v0.7.0...v[0.7.1]) (2024-09-02)

### Features

* **browse:** add useragent generator [#60](https://github.com/phodal/shire/issues/60) ([df595f4](https://github.com/phodal/shire/commit/df595f4d983a794a41840be289bd6ca119fe35bb))
* **compiler:** add JsonPath support for pattern actions and closed [#11](https://github.com/phodal/shire/issues/11) ([14bed16](https://github.com/phodal/shire/commit/14bed168ec74647072184b344bb5eb3e70b2a97c))
* **compiler:** add support for 'capture' and 'thread' pattern actions [#11](https://github.com/phodal/shire/issues/11) ([dc50586](https://github.com/phodal/shire/commit/dc50586aec094172179e469c6faf380d2bae876d))
* **core:** add cURL execution support and HTTP handler extension point [#11](https://github.com/phodal/shire/issues/11) ([2717014](https://github.com/phodal/shire/commit/271701435325c6856791ce17e81f64c8ff0119bb))
* **httpclient:** add functionality to convert cURL to HTTP request scratch file [#11](https://github.com/phodal/shire/issues/11) ([079968e](https://github.com/phodal/shire/commit/079968e4ff354e03d139751aaa2d6f5df28669be))
* **httpclient:** enhance CUrlConverter with variable support and testing adjustments [#11](https://github.com/phodal/shire/issues/11) ([9e97423](https://github.com/phodal/shire/commit/9e97423f583b1e4ec7becb75864945d5e88a4d70))
* **httpclient:** implement buildFullUrl function for RestClientRequest [#11](https://github.com/phodal/shire/issues/11) ([b49049b](https://github.com/phodal/shire/commit/b49049be5cb56d6214389f03d20fbc120f477e80))
* **httpclient:** Implement URL builder and scratch file creation [#11](https://github.com/phodal/shire/issues/11) ([9a2162e](https://github.com/phodal/shire/commit/9a2162e29d32f7c167649bf93aec7eff66551df9))
* **index:** add ShireEnvironmentIndex for indexing environment variables [#11](https://github.com/phodal/shire/issues/11) ([d43eb96](https://github.com/phodal/shire/commit/d43eb96eb51507b6bc8ea2b2e2bec9d57fdac31a))
* **kotlin-refactor:** implement Kotlin refactoring tool support [#58](https://github.com/phodal/shire/issues/58) ([ad6d1b2](https://github.com/phodal/shire/commit/ad6d1b253670b2d8a77eb901c0fb50830dccebc6))
* **kotlin:** implement structure providers for Kotlin plugin [#58](https://github.com/phodal/shire/issues/58) ([393b747](https://github.com/phodal/shire/commit/393b74799db5d56d09b769bb5b1f5862a5e33e73))
* **languages:** add shire-markdown module and update dependencies [#59](https://github.com/phodal/shire/issues/59) ([26fd92a](https://github.com/phodal/shire/commit/26fd92adf599b97fc599aa3d4c5be7678ddff000))
* **markdown:** add MarkdownPsiCapture for URL extraction [#59](https://github.com/phodal/shire/issues/59) ([ab9739b](https://github.com/phodal/shire/commit/ab9739bf0607d00d914d1265a56c94c490ce92b6))
* **runner:** Add LLM output to runFinish method and process handling [#60](https://github.com/phodal/shire/issues/60) ([5870ef8](https://github.com/phodal/shire/commit/5870ef8e00231abb12be061cf4c1a89964da01d1))
* **shire-kotlin:** add Java support for KotlinLanguageToolchainProvider [#58](https://github.com/phodal/shire/issues/58) ([e026929](https://github.com/phodal/shire/commit/e0269299c57e99f3dc7b15608657544148901004))
* **shirelang:** add crawl functionality and processor support [#59](https://github.com/phodal/shire/issues/59) ([ee5a3ea](https://github.com/phodal/shire/commit/ee5a3ea42b3b2b7feef4974002d5bb8b92817037))
* **shirelang:** add support for threading function execution [#60](https://github.com/phodal/shire/issues/60) ([0d31f9e](https://github.com/phodal/shire/commit/0d31f9e8636d9fa169eb6359dd9fff1c4ebc61ad))

### Bug Fixes

* **executor:** handle exceptions in ShireDefaultLlmExecutor [#60](https://github.com/phodal/shire/issues/60) ([da44e85](https://github.com/phodal/shire/commit/da44e858f32ea9bdb1c68b7b2b5d9f1db671676a))
* **plugin:** add Kotlin module support ([c3c1ecb](https://github.com/phodal/shire/commit/c3c1ecb98b03418b88415e26eb9742db82da3806))
* **shirelang:** add exception handling for LlmProvider streaming output [#60](https://github.com/phodal/shire/issues/60) ([a8272b2](https://github.com/phodal/shire/commit/a8272b2341e7770d2109ac666036e02d3d4bf103))

## [0.5.2](https://github.com/phodal/shire/compare/v0.5.0...v[0.5.2]) (2024-08-15)

### Bug Fixes

* add lost files ([e6f524d](https://github.com/phodal/shire/commit/e6f524dbf661b128270591bf92fe6a24795a9ae6))

### Features

* **compiler:** enhance query processor to lookup elements [#41](https://github.com/phodal/shire/issues/41) ([dbcdc6b](https://github.com/phodal/shire/commit/dbcdc6b89a8ab913245b2eed8858197c447bd96b))
* **git-plugin:** add Git4Idea plugin dependency [#41](https://github.com/phodal/shire/issues/41) ([f59cc83](https://github.com/phodal/shire/commit/f59cc83a6cb449f7de4f70d8e5cbe01a2064299e))
* **search:** add LLM reranker and reranker interface [#46](https://github.com/phodal/shire/issues/46) ([6a7b599](https://github.com/phodal/shire/commit/6a7b599be6ed44ac3e540865d3af6d995bebcf1d))
* **search:** add new ranking algorithm and update reranking methods [#46](https://github.com/phodal/shire/issues/46) ([b0b92b5](https://github.com/phodal/shire/commit/b0b92b555118543f5a10bd7187788b0db6e4de5a))
* **search:** enhance search functionality in SemanticService [#46](https://github.com/phodal/shire/issues/46) ([995331c](https://github.com/phodal/shire/commit/995331c76efafc9bf1deb5224c61044951a576c6))
* **search:** replace IndexEntry with ScoredEntry and add reranking functionality [#46](https://github.com/phodal/shire/issues/46) ([6a84387](https://github.com/phodal/shire/commit/6a843872b0e185028ae7f92f5aa0226efd3bd92b))

### Reverts

* Revert "refactor(core): update SecretPattern and SecretPatterns classes #47" ([1e1d556](https://github.com/phodal/shire/commit/1e1d5560303bd59dae2b73567c03b853041c33a7)), closes [#47](https://github.com/phodal/shire/issues/47)

## [0.4.8](https://github.com/phodal/shire/compare/v0.4.7...v[0.4.8]) (2024-08-02)

### Bug Fixes

* **guard:** improve regex pattern validation and update UK phone pattern [#47](https://github.com/phodal/shire/issues/47) ([bcc7e16](https://github.com/phodal/shire/commit/bcc7e162ad328445c707d4df8a0041ac23d391ad))

### Features

* **core:** add GuardScanner interface and ScanResult data class [#47](https://github.com/phodal/shire/issues/47) ([b32b7d8](https://github.com/phodal/shire/commit/b32b7d8e53dd52129ea2a2280fe4682bba3591b8))
* **guard:** add matching and masking functions, remove Joni dependency [#47](https://github.com/phodal/shire/issues/47) ([6a12233](https://github.com/phodal/shire/commit/6a12233170625ed147694167b8c71690649f0266))
* **scanner:** refactor scanner classes and update documentation [#47](https://github.com/phodal/shire/issues/47) ([a223428](https://github.com/phodal/shire/commit/a223428131c757fdf1bf2cde8a0877895020d13c))
* **schema-provider:** add CustomAgentSchemaFileProvider to factory list [#47](https://github.com/phodal/shire/issues/47) ([c8999e5](https://github.com/phodal/shire/commit/c8999e59d6ea9fac58a59e2864680d89ec163253))
* **search:** add dimensions parameter to embedInternal function ([494a8b5](https://github.com/phodal/shire/commit/494a8b56f27d8e0f90338457e3276321fac724dc))
* **secrets-detection:** rename secretType to description and add LocalModelBasedScanner [#47](https://github.com/phodal/shire/issues/47) ([091bacd](https://github.com/phodal/shire/commit/091bacdaea7fe70542b1016840670998e2649d38))
* **secrets-guard:** implement regex-based secret detection [#47](https://github.com/phodal/shire/issues/47) ([1eb2517](https://github.com/phodal/shire/commit/1eb2517e69f2b46066d41e13fc07539eb021ddb2))
* **secrets:** implement regex patterns for PII detection ([a718ac7](https://github.com/phodal/shire/commit/a718ac7c248c7de043f7c2b7c641ed162b7bd7a4))
* **security:** implement BanKeywordsScanner and Replacer interface [#47](https://github.com/phodal/shire/issues/47) ([47f2b69](https://github.com/phodal/shire/commit/47f2b697b857c39d76d85f1338b7128288fb4b99))
* **shirelang:** implement redact function for data masking [#47](https://github.com/phodal/shire/issues/47) ([ed28585](https://github.com/phodal/shire/commit/ed285856f36e443687fc8bc75f3a70084d75723f))

## [0.4.7](https://github.com/phodal/shire/compare/v0.4.6...v[0.4.7]) (2024-07-30)

### Bug Fixes

* **core:** enhance InsertUtil to validate offset range ([022d8e2](https://github.com/phodal/shire/commit/022d8e249da9b5aaa7e7166cc69de2269ff05e34))

### Features

* **core:** add UpdateEditorTextProcessor ([1d8b566](https://github.com/phodal/shire/commit/1d8b566e2b2b0da7ba98f88fecb9ec8be89214c2))
* **docs:** update lifecycle documentation and examples ([9ca64fe](https://github.com/phodal/shire/commit/9ca64fee7870c5b9365541e4d4a67391758fd83f))
* **git-provider:** implement lookupGitData and ShireVcsCommit updates [#41](https://github.com/phodal/shire/issues/41) ([2d56eaf](https://github.com/phodal/shire/commit/2d56eafbdbd15c6824da891366529da3e6282d49))
* **llm:** enhance OpenAILikeProvider to include temperature setting ([cbb6f1d](https://github.com/phodal/shire/commit/cbb6f1d15041520c19f8f2ffde54b8ae7aa1bbeb))
* **provider:** add GitQLDataProvider and update ShireQLDataProvider interface [#41](https://github.com/phodal/shire/issues/41) ([5af617e](https://github.com/phodal/shire/commit/5af617e2ebbabd0908c0ef9d9c82eead4886a8f7))
* **settings:** add temperature setting and UI component ([264211f](https://github.com/phodal/shire/commit/264211f5937a0d42f10d90a8dfee92aae29035ac))
* **shirelang:** add VcsStatementProcessor for commit info handling [#41](https://github.com/phodal/shire/issues/41) ([c71fe57](https://github.com/phodal/shire/commit/c71fe57d5dcf73a37298139a2ac1dee34b4f5c91))

## [0.4.6](https://github.com/phodal/shire/compare/v0.4.5...v[0.4.6]) (2024-07-24)

### Bug Fixes

* **compiler:** handle exceptions when finding files ([e85eb79](https://github.com/phodal/shire/commit/e85eb79923442db4a519c62fa78242862cd16d76))

### Features

* **compiler:** add support for preserving last output in function execution ([73739f5](https://github.com/phodal/shire/commit/73739f5bf2bd1ab4c1fa2b71979690cfd53fd64f))
* **compiler:** enhance afterStreaming execution flow ([c2b7227](https://github.com/phodal/shire/commit/c2b7227abe7f129cbad9f553c2f759928709ba77))
* **core:** add MarkdownPsiContextVariableProvider ([5418957](https://github.com/phodal/shire/commit/5418957719f848ac0e28aac345c8b0a2e95c3dd4))
* **core:** allow null values in compiledVariables and refactor file execution ([20eed68](https://github.com/phodal/shire/commit/20eed683f0d265bcbbef2e710afe0784f320134b))
* **core:** enhance MarkdownPsiContextVariableProvider for file context ([33b97da](https://github.com/phodal/shire/commit/33b97da7fbb7db91ae34c397f8caf1165266f819))
* **core:** enhance MarkdownPsiContextVariableProvider for HTML conversion ([451e61d](https://github.com/phodal/shire/commit/451e61d73e370601437af95c12ce6364e8efa094))
* **custom:** add custom SSE handling ([5735a58](https://github.com/phodal/shire/commit/5735a589b4f7e45b3a750dfc736135b578eb0e08))
* **docs:** update response routing for Java and dynamic input ([0bd650d](https://github.com/phodal/shire/commit/0bd650dc75d39a4a966d8873551cd33b29c8ef11))
* **runner:** enhance Shire runner to handle last output ([4e2804b](https://github.com/phodal/shire/commit/4e2804b6501a7086c3e0437e1bff6e9fcaa6f309))
* **searching:** add similarity threshold to search function ([dd8cd73](https://github.com/phodal/shire/commit/dd8cd73c9c9466f14551650a685fb2d5f0538f03))
* **search:** normalize embeddings and update search methods ([ad907ec](https://github.com/phodal/shire/commit/ad907ec2b27c822d3c8b07467f550618133a6ebc))
* **Shirelang:** enhance HobbitHole and ShireRunFileAction for dynamic interaction ([18f3679](https://github.com/phodal/shire/commit/18f3679711dea6dcc1588a1d921dc9339ad03279))
* **shirelang:** improve file not found error logging ([043488e](https://github.com/phodal/shire/commit/043488e6c1a81066d2dda95bcc661d932af7fff1))
* **ShireRunner:** enhance error handling for detachProcess ([d221082](https://github.com/phodal/shire/commit/d22108282ac9e2fedba2442986708b70b19b35cd))
* **testing:** add new test case for afterStreamingOnly functionality ([a91be0d](https://github.com/phodal/shire/commit/a91be0d2532bc7540ac3bdfefbcdfc707513622c))

## [0.4.5](https://github.com/phodal/shire/compare/v0.4.3...v[0.4.5]) (2024-07-19)

### Features

* **core:** implement equals and hashCode for IndexEntry ([2543805](https://github.com/phodal/shire/commit/25438059ca7ee50a2c47fba29a7e0cc8ef8ed677))
* **search:** add interface for similarity algorithms ([27c65c1](https://github.com/phodal/shire/commit/27c65c1c0dd85c7c91ddcfd72ba90470c03136f8))
* **search:** implement BM25 similarity algorithm and refactor SimilarChunkSearcher ([ef32475](https://github.com/phodal/shire/commit/ef3247552446f1218bd6a0ea30857cb952160933))

## [0.4.3](https://github.com/phodal/shire/compare/v0.4.2...v[0.4.3]) (2024-07-14)

### Bug Fixes

* **actions:** handle null hole in context menu actions ([4a04f35](https://github.com/phodal/shire/commit/4a04f356b16689514bdc4af10df30b8d136b8b6a))

### Features

* **compiler:** add Tee class for writing to files [#36](https://github.com/phodal/shire/issues/36) ([153cc93](https://github.com/phodal/shire/commit/153cc93097f162e96b2f37aa1011f3caf6178886))
* **interaction:** add PasteBoard interaction type ([33afb37](https://github.com/phodal/shire/commit/33afb37775288360865cb3e47b6a943bee1895bd))
* **middleware:** add append functionality and AppendProcessor [#36](https://github.com/phodal/shire/issues/36) ([7413368](https://github.com/phodal/shire/commit/74133686a19869e25ff84a5c0392c081c9df787d))
* **middleware:** expose and update compiledVariables across components [#36](https://github.com/phodal/shire/issues/36) ([c83ffbe](https://github.com/phodal/shire/commit/c83ffbe27659d96139ee3b7165aa6982495c1187))
* **provider:** add method support for JavaPsiQLInterpreter ([ad83803](https://github.com/phodal/shire/commit/ad838038fefac63ad64c4e24351e34db50319f89))
* **provider:** add PsiQLMethodCallInterpreter interface ([c9b6606](https://github.com/phodal/shire/commit/c9b6606d127d0a4e4f6df2d1c0229569272f5e25))
* **shirelang:** extend pipelineArg syntax and add contentTee test [#36](https://github.com/phodal/shire/issues/36) ([42031b7](https://github.com/phodal/shire/commit/42031b7065886b9e399b07497e5157c2d2cd5652))

## [0.4.2](https://github.com/phodal/shire/compare/v0.4.1...v[0.4.2]) (2024-07-09)

### Bug Fixes

* **git:** handle null data context in GitToolchainVariableProvider ([69c48cd](https://github.com/phodal/shire/commit/69c48cd74e444cebdbeadae31bcf84b71c11a0ed))

### Features

* **build:** add kotlinx-coroutines-core dependency ([836332b](https://github.com/phodal/shire/commit/836332bfdca2481f4762a331b7d82b207a4f374b))
* **run:** add console message before running configuration ([a27e899](https://github.com/phodal/shire/commit/a27e8993cd2090293d42764fb9215a7955853c84))
* **search:** add caching support to semantic search ([7ef7287](https://github.com/phodal/shire/commit/7ef7287ecd15b27726d456587942204ccb184005))

## [0.4.1](https://github.com/phodal/shire/compare/v0.3.0...v[0.4.1]) (2024-07-07)

### Bug Fixes

* **compiler:** handle null defaultTask in TaskRoutes.kt ([10cfc67](https://github.com/phodal/shire/commit/10cfc67464c249822a1abdc400bbadd32726537b))
* **completion:** update HobbitHoleValueCompletion to display action location with descriptions. ([71153b4](https://github.com/phodal/shire/commit/71153b4a8aed7d8c617b1ec37cd39762a04ca1cf))
* **config:** handle nullable Flow<String> in EditorInteractionProvider ([6086166](https://github.com/phodal/shire/commit/6086166d6e95bf7c1d24e13fba5e6587ed74d349))
* **core:** ensure thread safety in BaseCodeGenTask.kt ([4479e8a](https://github.com/phodal/shire/commit/4479e8aef4356f49ac6bb2ee7c5674415e3c6359))
* **run:** handle line markers for leaf elements only ([62f09f6](https://github.com/phodal/shire/commit/62f09f6399e03b5e14500b268341e99ad5ab80f1))
* **runner:** detach process handler in ShireRunner ([041c356](https://github.com/phodal/shire/commit/041c35682330af2257c80b8bb63377b0f5b72317))
* **shirelang:** check for null before adding action to toolsMenu ([289f578](https://github.com/phodal/shire/commit/289f57876eecea204acdabde87dec7016b5d4857))
* **shirelang:** update line marker provider to support front matter entries ([2e843a9](https://github.com/phodal/shire/commit/2e843a913b19fb201190212f5d35244064495274))

### Features

* **actions:** add method to set keymap shortcut ([f99686a](https://github.com/phodal/shire/commit/f99686a6017f9a50a506aa31a77f6902ff96adae))
* **code-completion:** refactor code completion task and add InsertUtil [#29](https://github.com/phodal/shire/issues/29) ([b02987e](https://github.com/phodal/shire/commit/b02987edb180f8dc4db36a607651d512fc3b6d1e))
* **compiler:** add CaseMatch functionality in PatternActionFunc [#29](https://github.com/phodal/shire/issues/29) ([0521198](https://github.com/phodal/shire/commit/05211984004d3e0b96e77c76a6a252e168eafd87))
* **compiler:** add save file functionality ([c6bbde6](https://github.com/phodal/shire/commit/c6bbde6e87f9aa065e7daa9832f6bacec1197878))
* **compiler:** add support for WHEN condition and VARIABLES ([593abd5](https://github.com/phodal/shire/commit/593abd56c897d527891ae0e365f177aad0809b18))
* **completion:** add PostProcessor completion provider ([4968586](https://github.com/phodal/shire/commit/49685869b416679d0c050d06d2bfa523e3266c6d))
* **completion:** add PSI context variables to VariableCompletionProvider [#29](https://github.com/phodal/shire/issues/29) ([564b0fe](https://github.com/phodal/shire/commit/564b0fe36aa318bb852b08a5e000edc05d315be5))
* **completion:** add QueryStatementCompletion provider ([696a306](https://github.com/phodal/shire/commit/696a3069f8e4302f94fdfed4c5b0d87e56511580))
* **core:** add code completion task and related changes [#29](https://github.com/phodal/shire/issues/29) ([34eb6fe](https://github.com/phodal/shire/commit/34eb6fec9f5d70b99068870382f7fe8f1cc7154c))
* **core:** add GitActionLocationEditor for commit menu ([48db3c4](https://github.com/phodal/shire/commit/48db3c4e1325ab93201fcde518cd4924cc58f2f8))
* **core:** add postExecute callback to code execution tasks [#29](https://github.com/phodal/shire/issues/29) ([af6567b](https://github.com/phodal/shire/commit/af6567bfdf418643d44af590ce1a266b422b8a92))
* **core:** add postExecute invocation and update interactionType in ShireDefaultRunner [#29](https://github.com/phodal/shire/issues/29) ([8bc4b8b](https://github.com/phodal/shire/commit/8bc4b8bf2b9ee1397286b1769db504b953e72c29))
* **core:** add reflection support for ToolchainVariable ([72ec463](https://github.com/phodal/shire/commit/72ec463441f50c0a65adc174c65e8a7fcdc86700))
* **docs:** add agent examples and documentation provider ([8d7aafe](https://github.com/phodal/shire/commit/8d7aafeb9107503fa3797700a214db19b37939de))
* **docs:** add examples for code comments, refactoring, CLI copilot, and commit message generation ([1e07f68](https://github.com/phodal/shire/commit/1e07f689fc50d0eaa4eff7c72339765d6b646dff))
* **EditorInteractionProvider:** enhance task creation and error handling [#29](https://github.com/phodal/shire/issues/29) ([e0f34a3](https://github.com/phodal/shire/commit/e0f34a381cc19847330881966d02956f891786f8))
* **git:** add commit message UI retrieval improvement in ShireVcsSingleAction ([a8f11de](https://github.com/phodal/shire/commit/a8f11de61dcc51fa55a128f05140a8e83223b73d))
* **input:** add custom input box action ([8ec40a4](https://github.com/phodal/shire/commit/8ec40a41517b17f5644268a9eb2ea885233377c6))
* **interaction:** add support for running code in Run panel ([7c4e8d5](https://github.com/phodal/shire/commit/7c4e8d5f4f798e73b01fa91a473afb66c3e1ec5c))
* **interaction:** improve code completion and generation tasks [#29](https://github.com/phodal/shire/issues/29) ([957e75d](https://github.com/phodal/shire/commit/957e75dbef245538472313595882c872093c1309))
* **interaction:** refactor code generation tasks and add BaseCodeGenTask [#29](https://github.com/phodal/shire/issues/29) ([0f84b3b](https://github.com/phodal/shire/commit/0f84b3bd80af33b07c5831ad5441b9df1ee9a7a8))
* **java:** add class structure representation and data builder [#29](https://github.com/phodal/shire/issues/29) ([90f2364](https://github.com/phodal/shire/commit/90f2364ffbbb45f73a6e09e801461080d8a9010a))
* **java:** add method caller and called method lookup [#29](https://github.com/phodal/shire/issues/29) ([6b46c00](https://github.com/phodal/shire/commit/6b46c001b5f0a209b8cfd65418205c5206698852))
* **java:** add methods to retrieve containing class and method ([b4c26ea](https://github.com/phodal/shire/commit/b4c26eaf868a1dfb9845d377b330b933d97eda61))
* **keyboard:** add support for setting keymap shortcuts ([4eda080](https://github.com/phodal/shire/commit/4eda0800f6b168c315b1ce72231a66508ef625ef))
* **lints:** add duplicate agent inspection ([7947d3c](https://github.com/phodal/shire/commit/7947d3cd92329d9e81a241682646dfdb566e0002))
* **logging:** improve error handling and logging in ShireActionStartupActivity ([e661e16](https://github.com/phodal/shire/commit/e661e161f51a7837cbdb88387bbc3b7533eb306e))
* **middleware:** add InsertNewlineProcessor ([73cebef](https://github.com/phodal/shire/commit/73cebef93bff5e71558938e8d6153f906276a895))
* **middleware:** add ParseCommentProcessor ([aafb938](https://github.com/phodal/shire/commit/aafb9386c5e9e55a54577c531cd770c83a557f5f))
* **provider:** add terminal location executor ([99c93a0](https://github.com/phodal/shire/commit/99c93a0da396940ddd1f2fe1c186474d609fbcce))
* **runner:** add support for user input in Shire configuration ([6ffebd6](https://github.com/phodal/shire/commit/6ffebd6a2fd0ab5004dde65526b2eb11de395dd1))
* **runner:** refactor ShireRunner to improve terminal task execution and error handling ([b236ad2](https://github.com/phodal/shire/commit/b236ad2f5c7b238c26e7e021d17991aaf312bc0f))
* **shirelang:** add icon support and improve line marker provider [#29](https://github.com/phodal/shire/issues/29) ([f12c199](https://github.com/phodal/shire/commit/f12c19900f307ff84e057b304564e0b5d37e5454))
* **shirelang:** add ShirePsiExprLineMarkerProvider for line marker support [#29](https://github.com/phodal/shire/issues/29) ([1a5c3ca](https://github.com/phodal/shire/commit/1a5c3ca56752179586baf390c85a4f21f2c4bd07))
* **shirelang:** refactor and improve pattern action processing [#29](https://github.com/phodal/shire/issues/29) ([57689a2](https://github.com/phodal/shire/commit/57689a2ae0e8c803bbeb6e646e95f39ec095a8e9))
* **shirelang:** update line marker provider for ShirePsiExpr ([6448aa1](https://github.com/phodal/shire/commit/6448aa154ccc9a6e02213cf664dc7d6aa84aa05d))
* **shirelang:** update line marker provider to support front matter entries ([1d4bb4b](https://github.com/phodal/shire/commit/1d4bb4b4c9403b88ede31b2ca38b92e1ce4d8fef))
* **terminal:** add input box popup for terminal action ([03dce26](https://github.com/phodal/shire/commit/03dce2625011eea61cd3b2b5e0860527a904229f))
* **terminal:** add shell command suggestion action ([958340c](https://github.com/phodal/shire/commit/958340c89a7dcc8e6e1eef190d31f409356be83c))
* **terminal:** add ShireTerminalAction for custom assistants ([01023de](https://github.com/phodal/shire/commit/01023de953d69115760d0b6c29a085f344a2f067))
* **terminal:** add TerminalToolchainVariableProvider ([8b13dc3](https://github.com/phodal/shire/commit/8b13dc39d88636f34196e7807c700d94f957feb3))
* **variable:** add BuiltinVariable and resolver ([f88af49](https://github.com/phodal/shire/commit/f88af49fe0bc197eb9d1443c0addafb1d7157667))
* **variable:** add SystemInfoVariable and resolver ([c1a54eb](https://github.com/phodal/shire/commit/c1a54eb4cdc1f06cbb0b47d7ce58e639d56404bd))
* **VariableCompletionProvider:** add icon to variable lookup elements [#29](https://github.com/phodal/shire/issues/29) ([7ae12c5](https://github.com/phodal/shire/commit/7ae12c5f071c1ec494f7410f482a4375a1eba01b))
* **variables:** add code smell detection and test data generation [#29](https://github.com/phodal/shire/issues/29) ([3763184](https://github.com/phodal/shire/commit/3763184da20ade0d3df8a9916730855438502f41))
* **variables:** add support for similar code search ([5fe7f8f](https://github.com/phodal/shire/commit/5fe7f8f2db96727312d56101e77abff9726e9138))
* **vcs): add Shirefeat VCS single(vcs:** action ([e982cec](https://github.com/phodal/shire/commit/e982cecd524b786fd0c30d12770587286082e3f4))

## [0.0.8](https://github.com/phodal/shire/compare/v0.0.7...v[0.0.8]) (2024-07-01)

### Bug Fixes

* **compiler:** wrap parsing operations in read actions ([97c8d15](https://github.com/phodal/shire/commit/97c8d156b78971f787bcfd202eb6f6aa3f030a06))
* **compiler:** wrap parsing operations in read actions ([76e1700](https://github.com/phodal/shire/commit/76e1700b7b213d73de07f53053df28c96bba7905))
* **completion:** fix code fence insertion in completion ([7beb240](https://github.com/phodal/shire/commit/7beb240a2eaafd997e85d82e52330ea5f5fb3cf2))
* **shirelang:** refine action body parsing in FrontmatterParser kt file ([32776cd](https://github.com/phodal/shire/commit/32776cdf389710c941010a3d117bd5076202d1b1))
* **shirelang:** update when condition syntax and test evaluation ([2ee436c](https://github.com/phodal/shire/commit/2ee436c73362d06ecb7dd1858c42d094f0205a34))

### Features

* **actions:** refactor action groups and context menu action ([900d613](https://github.com/phodal/shire/commit/900d613900ed6dd9785b8ea3a1b082065db18f54))
* **chat:** add ChatRole enum and FileGenerateTask for file output ([9bf98d5](https://github.com/phodal/shire/commit/9bf98d590d0c9a5e6726969009809fdefe02d4a8))
* **compiler:** add 'afterStreaming' feature and enhance pattern action processing [#24](https://github.com/phodal/shire/issues/24) ([bb21198](https://github.com/phodal/shire/commit/bb2119819f27bdc966dc0949f4b79681989bccaf))
* **compiler:** add FunctionStatementProcessor and refactor related classes [#24](https://github.com/phodal/shire/issues/24) ([1179c5e](https://github.com/phodal/shire/commit/1179c5e2089a1bd821e17dcd8d37a54c3a3977ca))
* **compiler:** add jsonpath support and modify error condition [#24](https://github.com/phodal/shire/issues/24) ([1913e4f](https://github.com/phodal/shire/commit/1913e4f994c208d624dbed7f70caff4e888e297e))
* **compiler:** enhance function statement processing and add new pattern actions [#24](https://github.com/phodal/shire/issues/24) ([054fec5](https://github.com/phodal/shire/commit/054fec5ae136a0507a715bd5c222351616e81c73))
* **compiler:** enhance statement processing in FunctionStatementProcessor [#24](https://github.com/phodal/shire/issues/24) ([56a551c](https://github.com/phodal/shire/commit/56a551c2b40ec86c59ee4ca2226040f5e0ef9a57))
* **compiler:** refactor function execution and improve logging [#24](https://github.com/phodal/shire/issues/24) ([8944045](https://github.com/phodal/shire/commit/8944045043e5b6b4b00f18e4c49c27f97aade484))
* **compiler:** refactor method invocation in FunctionStatementProcessor ([5b4911b](https://github.com/phodal/shire/commit/5b4911b38c8a9f1808a6028e8ea596c86c21d041))
* **compiler:** update function execution and case matching logic [#24](https://github.com/phodal/shire/issues/24) ([ae44272](https://github.com/phodal/shire/commit/ae44272ddab12bedfa89cd1efd6cc7dc53253809))
* **core:** enhance code parsing and saving [#24](https://github.com/phodal/shire/issues/24) ([0e95539](https://github.com/phodal/shire/commit/0e95539119bddbf5c1ffd5f95179fa28f6da3213))
* **core:** modify execute method to return string [#27](https://github.com/phodal/shire/issues/27) ([aeaf5d4](https://github.com/phodal/shire/commit/aeaf5d488b98da2db5adccc1d8159f6f8912dff8))
* **core:** refactor LlmProvider and related classes to shirecore package [#27](https://github.com/phodal/shire/issues/27) ([92e4026](https://github.com/phodal/shire/commit/92e4026c5407c9fa59e1854e24f7b5712797dcb3))
* **core:** update InteractionType and improve coroutine handling ([fffda5c](https://github.com/phodal/shire/commit/fffda5cc122eb48f60dc1063648e9aa81f22e7ec))
* **custom:** add custom SSE processor and JSON response callback [#10](https://github.com/phodal/shire/issues/10) ([6f75068](https://github.com/phodal/shire/commit/6f750687997d7660c73be3aa38781871da0f9a27))
* **docs:** add custom AI agent quickstart guide [#10](https://github.com/phodal/shire/issues/10) ([03f777b](https://github.com/phodal/shire/commit/03f777bf3437316259e9b932c0c01aada1592881))
* **docs:** add IDE note and update GitToolchainVariableProvider [#27](https://github.com/phodal/shire/issues/27) ([d59fcb8](https://github.com/phodal/shire/commit/d59fcb804185a9eb94a71b297b741ecf7e18f7e2))
* **editor:** add smart code insertion feature [#24](https://github.com/phodal/shire/issues/24) ([788051f](https://github.com/phodal/shire/commit/788051f45f39712844536c4b8460c440a403b799))
* **folding:** add support for query statements and block comments ([32e1da8](https://github.com/phodal/shire/commit/32e1da8e9287509d7e561aeda12fa081eaa2ee21))
* **grammar:** add support for QUOTE_STRING in agentId ([3fdf55c](https://github.com/phodal/shire/commit/3fdf55c59a306e3884a8fd37aecee94c610b0a68))
* **InteractionType:** add new interaction type and change defaults ([b944f93](https://github.com/phodal/shire/commit/b944f935b4a891297d03933fbf7517e0eced93f3))
* **java:** add smart insert method in JavaCodeModifier ([42d8326](https://github.com/phodal/shire/commit/42d8326c95c5df03d34beb11b8b9a08374fc6884))
* **lexer:** add brace level tracking and state transitions [#16](https://github.com/phodal/shire/issues/16) ([882444a](https://github.com/phodal/shire/commit/882444a62f0053e44705a058632a4c73abd784ef))
* **lexer:** add support for multiple front matter variables [#16](https://github.com/phodal/shire/issues/16) ([9eb9f8d](https://github.com/phodal/shire/commit/9eb9f8d6ed8761f33f3f3736e51b285667004867))
* **middleware:** Add console parameter to PostProcessor execute methods [#24](https://github.com/phodal/shire/issues/24) ([ebcbd94](https://github.com/phodal/shire/commit/ebcbd944182804dfa44050f46ef38e42f658eb96))
* **middleware:** add FormatCode functionality ([6905f4f](https://github.com/phodal/shire/commit/6905f4f25dea2a7757811a8b14076f5afd0e045c))
* **middleware:** add OpenFileProcessor to handle file opening [#24](https://github.com/phodal/shire/issues/24) ([99a2ec2](https://github.com/phodal/shire/commit/99a2ec2efb2f04194c927ccab4ef767466f9867c))
* **middleware:** add RunCodeProcessor and ExtensionPoint for file execution [#24](https://github.com/phodal/shire/issues/24) ([0b9f065](https://github.com/phodal/shire/commit/0b9f065f7b4925158429f43152429e38b38dcd21))
* **middleware:** enhance code execution, file saving and verification [#24](https://github.com/phodal/shire/issues/24) ([ed7ca3b](https://github.com/phodal/shire/commit/ed7ca3baaa7af894af532499d33c86b704af4d9f))
* **parser:** add parentheses detection in method calls [#16](https://github.com/phodal/shire/issues/16) ([29abe86](https://github.com/phodal/shire/commit/29abe86737527e152a686bcf32c89e74849aa574))
* **plugin:** add Python support to Shirelang plugin [#24](https://github.com/phodal/shire/issues/24) ([7da1a1f](https://github.com/phodal/shire/commit/7da1a1ff6e798613740924fefb21cbb58cc6e92e))
* **QueryStatementProcessor:** enhance error logging for method or field not found [#16](https://github.com/phodal/shire/issues/16) ([9dd696f](https://github.com/phodal/shire/commit/9dd696fac416c077712e5a998242af664e700436))
* **run-code:** add CLI execution support for Python, JavaScript, and Ruby files [#24](https://github.com/phodal/shire/issues/24) ([0619ecb](https://github.com/phodal/shire/commit/0619ecb511d8976a5b4535df1f6debbf035388b1))
* **runFile:** wrap file search in runReadAction for thread safety ([d8324c3](https://github.com/phodal/shire/commit/d8324c350f2e7580d613cde6e90e2bada8e1bc7a))
* **runner:** refactor interaction handling in IDE locations [#27](https://github.com/phodal/shire/issues/27) ([e51dc98](https://github.com/phodal/shire/commit/e51dc98a938b291b67dc68fcd9e47b05ba25486b))
* **runners:** add HobbitHole to ShireRunner classes [#24](https://github.com/phodal/shire/issues/24) ([9e55bd1](https://github.com/phodal/shire/commit/9e55bd1d97de1c5ce2e9151a65c05cb7950ab29f))
* **schema:** add Shire Custom Agent schema provider factory [#16](https://github.com/phodal/shire/issues/16) ([dc03d0c](https://github.com/phodal/shire/commit/dc03d0c08d50c146ecd9539730db84c497f734a8))
* **shire-core:** implement EditorInteractionProvider and add Shire Toolchain Variable doc [#27](https://github.com/phodal/shire/issues/27) ([aff5380](https://github.com/phodal/shire/commit/aff5380c05edf560a202de52947812b1d58994fa))
* **shirelang:** add default condition and new test for afterStreaming [#24](https://github.com/phodal/shire/issues/24) ([e5a28cb](https://github.com/phodal/shire/commit/e5a28cbe8e1a5036dd24080883b8c3ce71590224))
* **shirelang:** add file execution support and improve condition handling ([d37e306](https://github.com/phodal/shire/commit/d37e3060f47d5b8e3ff290d18ac2cc59d7beebd8)), closes [#24](https://github.com/phodal/shire/issues/24)
* **shirelang:** add new lifecycle keywords and update grammar [#24](https://github.com/phodal/shire/issues/24) ([c957282](https://github.com/phodal/shire/commit/c9572825a23a9c2c3ded709427784d3f736f7532))
* **shirelang:** add new lifecycle keywords to syntax highlighter ([3e43d38](https://github.com/phodal/shire/commit/3e43d38282bc35e46ff6785f03f0371f8ee9f75b))
* **shirelang:** add support for finish flags in output control flow [#24](https://github.com/phodal/shire/issues/24) ([99f1023](https://github.com/phodal/shire/commit/99f10230c6c856d6fe429c5c8c8b996d835525ee))
* **shirelang:** enhance pattern action processing and test handling [#24](https://github.com/phodal/shire/issues/24) ([cb59bfb](https://github.com/phodal/shire/commit/cb59bfb8233c027ace91680c750ffe38a63a778b))
* **TaskRoutes:** make defaultTask optional and refactor execution logic [#24](https://github.com/phodal/shire/issues/24) ([5787cca](https://github.com/phodal/shire/commit/5787cca2ad6fad9e00b59e5108e701a267d5f5e0))
* **testing:** add success condition and improve function execution [#24](https://github.com/phodal/shire/issues/24) ([6eadfda](https://github.com/phodal/shire/commit/6eadfdaae2deb177158c134f4b72ddee51397f69))
* **variable resolver:** add project context to resolveAll method [#27](https://github.com/phodal/shire/issues/27) ([b5f05f9](https://github.com/phodal/shire/commit/b5f05f940c3619db1c5ee38e9ca235ce4fdc6ffb))
* **variable-resolver:** add support for toolchain variables [#27](https://github.com/phodal/shire/issues/27) ([fdbb4c9](https://github.com/phodal/shire/commit/fdbb4c91b01f9ef78b0a0752d545c70f801fab02))
* **variables:** add ToolsetVariable and ToolsetVariableProvider [#27](https://github.com/phodal/shire/issues/27) ([c1da8a5](https://github.com/phodal/shire/commit/c1da8a5d3ac7be9c8452f2f9c34bd5aa49a5dd86))
* **vcs:** add dynamic actions to VCS action group [#24](https://github.com/phodal/shire/issues/24) ([9018da5](https://github.com/phodal/shire/commit/9018da5056323b148a9c5d797c9f994efb1efd93))
* **vcs:** add ShireVcsActionGroup for dynamic actions ([902bc2a](https://github.com/phodal/shire/commit/902bc2ab24df996bfc1fd7bd47eb5204b5dc916c))
* wrap code blocks with appropriate application run actions [#24](https://github.com/phodal/shire/issues/24) ([55f7495](https://github.com/phodal/shire/commit/55f7495db0c4a23adb3e736545a61dc2d6555ee4))

## [0.0.7](https://github.com/phodal/shire/compare/v0.0.6...v[0.0.7]) (2024-06-24)

### Bug Fixes

* **grammar:** update frontMatterArray syntax in ShireParser.bnf [#16](https://github.com/phodal/shire/issues/16) ([428033c](https://github.com/phodal/shire/commit/428033cb0b365e23e2e6fd77981ac38208c732ad))
* **pattern-searcher:** handle invalid regex and refactor code [#18](https://github.com/phodal/shire/issues/18) ([b00cd54](https://github.com/phodal/shire/commit/b00cd54bc1d9eb82a2dd259e2617f89b33ff6831))

### Features

* **codemodel:** add FileStructure and VariableStructure classes [#14](https://github.com/phodal/shire/issues/14) ([322e897](https://github.com/phodal/shire/commit/322e89770f2ef3b93804f514705c0d5f220a4111))
* **codemodel:** add MethodStructureProvider and related modifications [#14](https://github.com/phodal/shire/issues/14) ([29eedb9](https://github.com/phodal/shire/commit/29eedb9f5c00bcdcb78554f2c0c4e42d74dd78e2))
* **codemodel:** add VariableStructureProvider and FileStructureProvider, refactor MethodStructureProvider [#14](https://github.com/phodal/shire/issues/14) ([4be010b](https://github.com/phodal/shire/commit/4be010b8ca205aba73159d8436b1ab0d9049daa8))
* **codemodel:** enhance ClassStructure and MethodStructure formatting [#14](https://github.com/phodal/shire/issues/14) ([07488b0](https://github.com/phodal/shire/commit/07488b029ba653957738d226eb464f1270b64ebc))
* **codemodel:** update FileStructure and add DirectoryStructure [#14](https://github.com/phodal/shire/issues/14) ([076368e](https://github.com/phodal/shire/commit/076368ed87806d32bbfe4453ccdca613ad9a8f22))
* **compiler:** add error handling for function arguments ([376691f](https://github.com/phodal/shire/commit/376691f8ad251658282c69e62525cdcfc8ccee3d))
* **compiler:** add execution logic for variable pattern functions [#16](https://github.com/phodal/shire/issues/16) ([3542461](https://github.com/phodal/shire/commit/354246175027b6ff0cd8df14c1af0e3cd53edb0f))
* **compiler:** add operator handling in QueryStatementProcessor [#16](https://github.com/phodal/shire/issues/16) ([fc9f89d](https://github.com/phodal/shire/commit/fc9f89d6664e475f85227724eebf2496d8f26c16))
* **compiler:** add PatternSearcher for file matching by regex [#18](https://github.com/phodal/shire/issues/18) ([d129e40](https://github.com/phodal/shire/commit/d129e406478cfaba891b8ea57a608149da7796f9))
* **compiler:** add query statement support in pattern action [#16](https://github.com/phodal/shire/issues/16) ([12fd4fd](https://github.com/phodal/shire/commit/12fd4fda5671881128059ccdb844f12fa210f8dc))
* **compiler:** add regex support and array handling in PatternActionFunc [#18](https://github.com/phodal/shire/issues/18) ([aebf478](https://github.com/phodal/shire/commit/aebf478ba9b04a9ac4240809b7699a6edd6ddc73))
* **compiler:** add support for query statements in Shire language [#16](https://github.com/phodal/shire/issues/16) ([824c9f0](https://github.com/phodal/shire/commit/824c9f009ca0e428779703d30daf65ed7db65137))
* **compiler:** add Value class and evaluate function in ShireExpression [#16](https://github.com/phodal/shire/issues/16) ([8b04548](https://github.com/phodal/shire/commit/8b0454857ef01d31e669f69488d7ba277005413c))
* **compiler:** enhance QueryStatementProcessor functionality [#16](https://github.com/phodal/shire/issues/16) ([ddc2ff5](https://github.com/phodal/shire/commit/ddc2ff51e6b38d17d4de7db9a31de2366537e4b3))
* **compiler:** handle null arguments and improve error handling [#16](https://github.com/phodal/shire/issues/16) ([59502be](https://github.com/phodal/shire/commit/59502be32bcd45294fc884be19728eecdbe2327a))
* **compiler:** improve pattern action execution and testing [#18](https://github.com/phodal/shire/issues/18) ([4cada42](https://github.com/phodal/shire/commit/4cada4234799def70e7fc64534d8a7b50bd6acc2))
* **compiler:** improve pattern handling and file loading in ShireCompiler [#18](https://github.com/phodal/shire/issues/18) ([4f3062e](https://github.com/phodal/shire/commit/4f3062e4e3b4ca5e95db03a5c48c9f4f12ed424b))
* **compiler:** refactor built-in methods to enum class [#18](https://github.com/phodal/shire/issues/18) ([07e0475](https://github.com/phodal/shire/commit/07e0475851e59c0778254d6522666705ba78f58a))
* **compiler:** refactor pattern action classes and move to ast package [#18](https://github.com/phodal/shire/issues/18) ([35a5515](https://github.com/phodal/shire/commit/35a55153df29b82c715d010237ef0bf6ab916349))
* **compiler:** refactor query statement parsing and improve documentation [#16](https://github.com/phodal/shire/issues/16) ([6e385d5](https://github.com/phodal/shire/commit/6e385d58b2055f8d8faa0c37a308bd00d8f53f5d))
* **compiler:** refactor template compilation and variable resolution ([93d7536](https://github.com/phodal/shire/commit/93d75369b19e13938ab604e3be7d08f42c31db90)), closes [#18](https://github.com/phodal/shire/issues/18)
* **compiler:** refactor VariableStatement to VariableElement and add new PatternActionFunc subclasses [#16](https://github.com/phodal/shire/issues/16) ([6c9c9f3](https://github.com/phodal/shire/commit/6c9c9f3eddc5f33130fb6401250ff54d00571495))
* **console:** add console output for Shirelang execution and error handling [#18](https://github.com/phodal/shire/issues/18) ([b9e4bb1](https://github.com/phodal/shire/commit/b9e4bb19e8b04b828513ae5066ee4169e73b8b2d))
* **core:** add comment and refactor code in CustomAgent, ClassStructure, ShireExpression ([d5c0573](https://github.com/phodal/shire/commit/d5c05735dc7680c9cfc1060986f29c683a7da425))
* **docs, core, languages:** add design samples and method context variables ([bf99f08](https://github.com/phodal/shire/commit/bf99f086f14796d42a82b92d801a8968eac958e7))
* **FrontMatterType:** add QUERY_STATEMENT subclass [#16](https://github.com/phodal/shire/issues/16) ([a7ccc1a](https://github.com/phodal/shire/commit/a7ccc1a2629762fb11e0507bdc5188790610c997))
* **java-toolchain:** add Maven support and refactor tech stack detection [#18](https://github.com/phodal/shire/issues/18) ([9c14272](https://github.com/phodal/shire/commit/9c1427233e41d329add38933c399eb1110b80385))
* **java-toolchain:** refactor Maven build tool functionality into separate class [#18](https://github.com/phodal/shire/issues/18) ([39f4783](https://github.com/phodal/shire/commit/39f47837a79b159c1db04c2c7badfd1dd2863264))
* **java-toolchain:** replace JavaTasksUtil with GradleTasksUtil [#18](https://github.com/phodal/shire/issues/18) ([83c5e00](https://github.com/phodal/shire/commit/83c5e00f642226c5a0fb2b16d9e9d303bc666afe))
* **java:** enhance RelatedClassesProvider to support PsiClass [#14](https://github.com/phodal/shire/issues/14) ([f39aa4e](https://github.com/phodal/shire/commit/f39aa4e59f8472e7989c240c94ec061f5fdb36be))
* **java:** refactor method name and add JavaCodeModifier [#14](https://github.com/phodal/shire/issues/14) ([319cd74](https://github.com/phodal/shire/commit/319cd740b96e5217d27f92495dc7d9023b1f9410))
* **parser:** add 'and' operator and improve comment handling [#16](https://github.com/phodal/shire/issues/16) ([153c5a4](https://github.com/phodal/shire/commit/153c5a447fbe6ec575f04a5951fb917ce9a3a083))
* **parser:** add support for comments in ShireParserDefinition [#16](https://github.com/phodal/shire/issues/16) ([dbdf410](https://github.com/phodal/shire/commit/dbdf4108077c40d6440b71dd1710acf8e5858ba7))
* **parser:** remove whitespace support in query and from clauses [#16](https://github.com/phodal/shire/issues/16) ([9ac9d82](https://github.com/phodal/shire/commit/9ac9d8210727d584a77a72ff2806ce3730aa2c84))
* **parser:** update grammar and lexer for query expressions [#16](https://github.com/phodal/shire/issues/16) ([7fd6746](https://github.com/phodal/shire/commit/7fd67469b351e12af06d32770ecf0a43b64e00ec))
* **parsing:** update grammar and lexer for query expressions [#16](https://github.com/phodal/shire/issues/16) ([383e1ac](https://github.com/phodal/shire/commit/383e1ac48807f9b7befbfaa0f47344eb953e84d0))
* **pattern-action:** add array handling in Grep function [#18](https://github.com/phodal/shire/issues/18) ([9a76f1c](https://github.com/phodal/shire/commit/9a76f1cd3f5237534bae63d69937e7d1444e1f40))
* **plugin:** add Shire plugin configuration and enhance symbol lookup [#16](https://github.com/phodal/shire/issues/16) ([d25e77b](https://github.com/phodal/shire/commit/d25e77bac3f17cd0a9bb0964929b906dbe6ceb4b))
* **QueryStatementProcessor:** implement Expression and String cases ([915dd81](https://github.com/phodal/shire/commit/915dd81ddb7c710263d8e0f38f82514239d1007e))
* **QueryStatementProcessor:** implement operator and type evaluation [#16](https://github.com/phodal/shire/issues/16) ([4369408](https://github.com/phodal/shire/commit/4369408a74328b8a46418122f9e5a4e944f71e8e))
* remove saql project from build ([53d4d1f](https://github.com/phodal/shire/commit/53d4d1f919cf2219a9fbba6c053cc55b6af39047))
* **runner:** add system info variable resolution [#16](https://github.com/phodal/shire/issues/16) ([9f5d86b](https://github.com/phodal/shire/commit/9f5d86b91c29da377bcfe9eda39f402f84fa581d))
* **runner:** enhance data fetching methods in SystemInfoVariable ([411ebf4](https://github.com/phodal/shire/commit/411ebf4840152624f15a6800478bcf5715fdbf74))
* **saql:** add lexer and parser for Shire SQL [#16](https://github.com/phodal/shire/issues/16) ([a4479c3](https://github.com/phodal/shire/commit/a4479c32b9c72a6f6c57e8be490d825e8bf7fcfb))
* **saql:** add saql project and related files [#16](https://github.com/phodal/shire/issues/16) ([572694e](https://github.com/phodal/shire/commit/572694e1a129f2786a8e1e0fd15ff5584f2cea43))
* **saql:** enable getSqlTable methods in SAQLParser ([16d1c3c](https://github.com/phodal/shire/commit/16d1c3c93e45304038ae5d4a726097f167dd425c))
* **saql:** refactor Saql language support and add new classes [#16](https://github.com/phodal/shire/issues/16) ([cad778d](https://github.com/phodal/shire/commit/cad778dd28a51b8d5d931af54378189ec7df031f))
* **saql:** refactor Saql language support and add new classes [#16](https://github.com/phodal/shire/issues/16) ([0b00f60](https://github.com/phodal/shire/commit/0b00f60c65bac04b4bf80b095da22817ae638825))
* **shirelang:** add ShireCommenter and update lexer and parser [#16](https://github.com/phodal/shire/issues/16) ([916a7d7](https://github.com/phodal/shire/commit/916a7d758e09a77615f9f34f894914f882df6be4))
* **shirelang:** enhance FrontMatterType subclasses and refactor pattern action processing [#18](https://github.com/phodal/shire/issues/18) ([4777ee3](https://github.com/phodal/shire/commit/4777ee34b2cb65c26c7d506a25d694bb67c90b8b))
* **shirelang:** improve query expression and statement processing [#16](https://github.com/phodal/shire/issues/16) ([62cd708](https://github.com/phodal/shire/commit/62cd708d59179e40407c0bda184eb82baf7968a2))
* **shirelang:** refactor methods and improve error handling [#18](https://github.com/phodal/shire/issues/18) ([9b170ee](https://github.com/phodal/shire/commit/9b170ee80e973f6fe5f6b3c93c684290826210b5))
* **ShireLang:** update grammar rules and lexer definitions [#16](https://github.com/phodal/shire/issues/16) ([29cc1b1](https://github.com/phodal/shire/commit/29cc1b1364981f6548c08b4a7933ffc63d96fa09))
* **syntax-highlighter:** add new keywords to ShireSyntaxHighlighter [#16](https://github.com/phodal/shire/issues/16) ([9d937ec](https://github.com/phodal/shire/commit/9d937ec7dd5d7b1424f5ef6814af09f711cc9c68))
* **testing:** add DefaultShireSymbolProvider and update ShireQueryExpressionTest ([e756f77](https://github.com/phodal/shire/commit/e756f77af2a8b23b6b3ac136982b5a234b82d148)), closes [#16](https://github.com/phodal/shire/issues/16)
* **variable-resolver:** add date and time to SystemInfoVariableResolver ([bc3356b](https://github.com/phodal/shire/commit/bc3356b694f63036fb2b06860b3631097a87966f))
* **variable-resolver:** refactor variable resolution system [#18](https://github.com/phodal/shire/issues/18) ([a96c00e](https://github.com/phodal/shire/commit/a96c00ee704f545c270ef1a2333ffab33ab5904c))
* **VariablePatternActionExecutor:** add project, editor, and hole as class properties [#18](https://github.com/phodal/shire/issues/18) ([54da7a6](https://github.com/phodal/shire/commit/54da7a6d47c60b4c1cce75a9d833049b745c24c1))
* **variable:** refactor PsiVariable to PsiContextVariable [#18](https://github.com/phodal/shire/issues/18) ([6e5e176](https://github.com/phodal/shire/commit/6e5e176ef30e4ae83a93bfba07386dfb4773812d))

### Performance Improvements

* **build:** optimize Gradle build performance in GitHub Actions ([f09254d](https://github.com/phodal/shire/commit/f09254d1bd81a47a47794545d5abb9e0988b78bc))

## [0.0.6](https://github.com/phodal/shire/compare/v0.0.4...v[0.0.6]) (2024-06-16)

### Bug Fixes

* **java:** fix null check for JavaSdkType in JavaToolchainProvider ([fb8ad5f](https://github.com/phodal/shire/commit/fb8ad5f88d980f14505f8af1eb798de733438b81))
* **java:** handle null psiElement in resolveVariableValue ([ba34428](https://github.com/phodal/shire/commit/ba34428e95ca67e1373853be6e590d5ed4cf1eb9))
* **test:** update file patterns in test data ([bfbe463](https://github.com/phodal/shire/commit/bfbe46370b41a4c7f2c10f8d48f3446c97866b47))

### Features

* **actions:** add WhenConditionValidator for dynamic actions ([2e1c06a](https://github.com/phodal/shire/commit/2e1c06a1088c0c69cb471e4ae90af020f9147c36))
* **compiler:** add support for method call with arguments ([1cb74e3](https://github.com/phodal/shire/commit/1cb74e3771f265266480fa35daa18053630883ff))
* **compiler:** update HobbitHole variables to use list of conditions ([dd9dc0a](https://github.com/phodal/shire/commit/dd9dc0ae5638f0693dd3c651705d5da6e17f0dce))
* **completion:** add support for when condition functions ([35e13aa](https://github.com/phodal/shire/commit/35e13aa45c33886252ed31f9cde1af2accd154ff))
* **core:** add CodeStructVariableProvider for code struct generation [#14](https://github.com/phodal/shire/issues/14) ([9a6c573](https://github.com/phodal/shire/commit/9a6c5739066fd4cd2f0a4f61e0683919d89bb6b8))
* **frontmatter:** add support for logical OR expressions in frontmatter parsing ([3c0f5dc](https://github.com/phodal/shire/commit/3c0f5dc9e48aa7c4ec296cfab95cd5a5e59d3f29))
* **grammar:** add support for velocity expressions ([c71fb07](https://github.com/phodal/shire/commit/c71fb07f638afa52279cbc4921733c17c52d6cc7))
* **highlight:** add 'WHEN' keyword support ([abdcb6c](https://github.com/phodal/shire/commit/abdcb6cbfc46d5a12f81ff631304b47cac36a392))
* **java:** add JavaCodeStructVariableProvider for code struct generation [#15](https://github.com/phodal/shire/issues/15) ([378f44e](https://github.com/phodal/shire/commit/378f44ec4a4f4779cbdcc0329568257e1a519adb))
* **runner:** add SymbolResolver for variable resolution [#14](https://github.com/phodal/shire/issues/14) ([ed03e25](https://github.com/phodal/shire/commit/ed03e25211dc222a92dca99269150af5a4fbf351))
* **search:** add TfIdf class for text analysis [#14](https://github.com/phodal/shire/issues/14) ([55e94a6](https://github.com/phodal/shire/commit/55e94a6f109dd4677f985f605119d133045c75c6))
* **shire:** add PatternFun Cat subclass ([ca2b531](https://github.com/phodal/shire/commit/ca2b53117d2ca0c512bf182d8d83fb55922ee8bb))
* **template:** add Shire Action template and action [#14](https://github.com/phodal/shire/issues/14) ([0493d23](https://github.com/phodal/shire/commit/0493d2363006ae25073272d8035d0851aee15824))
* **tokenizer:** add TermSplitter and StopwordsBasedTokenizer [#14](https://github.com/phodal/shire/issues/14) ([a774f48](https://github.com/phodal/shire/commit/a774f48a17c64553ab417763511b230dfaf73b18))

## [0.0.4](https://github.com/phodal/shire/compare/2b4a6f06733149d0cd9763e2d4719a048fa37ce3...v[0.0.4]) (2024-06-11)

### Bug Fixes

* **build:** remove unnecessary project dependency ([4628e8b](https://github.com/phodal/shire/commit/4628e8b8b992b9904f68fd46d5c0117af9c51418))
* **build:** update paths for plugin directory in workflows ([a0b6570](https://github.com/phodal/shire/commit/a0b657083ec8d49d65dff87f4efd003848aac7ed))
* **parser:** fix filenameRules regex in ShireFmObject test ([85de5bd](https://github.com/phodal/shire/commit/85de5bdf5964886d6568aee24b61a1ff2e873da2))
* **parser:** fix filenameRules regex in ShireFmObject test ([74783a1](https://github.com/phodal/shire/commit/74783a1d7dfb15b956132f68e5316ba9775c0512))
* **release:** update gradle task path for patching changelog ([201c458](https://github.com/phodal/shire/commit/201c458e09399435039ade13fac81a0d43d26754))

### Features

* **action:** add ShireAction and ShireActionRegister interfaces ([e5ed7b8](https://github.com/phodal/shire/commit/e5ed7b8577e577717c26bb44520e4099dc57cb37))
* **actions:** add context and intent action retrieval ([1be1fda](https://github.com/phodal/shire/commit/1be1fda1b58f6d1e8f7a9e535ab1fedff0c17a15))
* **agent:** add custom agent response actions and configurations ([d6b7501](https://github.com/phodal/shire/commit/d6b7501a707c0c0172c6fc2a44db8dfc51a03569))
* **codeedit:** add CodeModifier interface for code editing ([3bcdf44](https://github.com/phodal/shire/commit/3bcdf44b35840e90af31045c0a387c93fbf2f38a))
* **codemodel:** add ClassStructureBuilder, ClassStructureProvider, and FormatableElement ([5504468](https://github.com/phodal/shire/commit/55044689e759e1ed7de6f4c1ca9c71ae19cee83e))
* **compiler:** add ElementStrategy for auto selecting parent block element ([4f5118b](https://github.com/phodal/shire/commit/4f5118b68a75af91262a2d5540e83c9dd3a1e920))
* **compiler:** add FrontmatterParser for dynamic action configuration ([12bb82a](https://github.com/phodal/shire/commit/12bb82a23275910b9195655ae9cdee16462a585c))
* **compiler:** add ShellRunService for running shell scripts ([63ab840](https://github.com/phodal/shire/commit/63ab840a6844d7a7a608aa11126da80863603a10))
* **compiler:** add support for browsing URLs ([3b63e52](https://github.com/phodal/shire/commit/3b63e52e2ff4debc066c6ae17de5ae4218bc9edd))
* **compiler:** add support for DATE type in front matter ([3062445](https://github.com/phodal/shire/commit/3062445a58cba099b1c3031a72bf8ec7247132f4))
* **compiler:** add support for filename rules ([c9e860c](https://github.com/phodal/shire/commit/c9e860c9bd14f7b8dc88b0f24792c1aacba19c56))
* **compiler:** add support for front matter configuration ([aada8af](https://github.com/phodal/shire/commit/aada8afdd3efaeb25504e3f7aa81350cb1c11080))
* **compiler:** add support for frontmatter parsing ([b80cee1](https://github.com/phodal/shire/commit/b80cee1da7c9e1a629777abcd4e9e202799e6898))
* **compiler:** add support for head and tail functions ([b3f0aa0](https://github.com/phodal/shire/commit/b3f0aa024408ff8add6091b8f2f2204d5b538aaf))
* **compiler:** add support for loading custom agents ([62357fe](https://github.com/phodal/shire/commit/62357feadc08f20ccfc3665e3b8498b65d4a3383))
* **compiler:** add support for print function ([511cdde](https://github.com/phodal/shire/commit/511cddef30a3d4169cb1679a0ecd2623da082d02))
* **compiler:** add support for replace pattern function ([9d4160f](https://github.com/phodal/shire/commit/9d4160f75700f130370a5112c4e1e3f1f43f6b1c))
* **compiler:** add support for variables in HobbitHole ([0178647](https://github.com/phodal/shire/commit/01786479ab1b3c524489afcbd12cc2bd6cc8ed47))
* **completion:** add basic completion for project run tasks ([e8c6578](https://github.com/phodal/shire/commit/e8c6578c4ce9a06a567bf4301b50041792286f61))
* **completion:** add completion providers for code fence languages, file references, variables, agents, commands, and more ([4ef7ab4](https://github.com/phodal/shire/commit/4ef7ab49a09f58fdf9dbd379e41f64f16dad7aaa))
* **completion:** add completion support for Git revisions ([c977564](https://github.com/phodal/shire/commit/c9775640d40ae7818a2b8c339195a22e50dbc7af))
* **completion:** add Hobbit Hole code completion ([4fd6e41](https://github.com/phodal/shire/commit/4fd6e419b9ece791a1194a6dd17a256d56e944b2))
* **core:** add commit functionality to RevisionProvider ([7be17f1](https://github.com/phodal/shire/commit/7be17f1cb0136bb177a478a5bc1a51e3cf91d543))
* **core:** add DAG support with topological sort and cycle detection ([c01ada5](https://github.com/phodal/shire/commit/c01ada5760574a31ada3b6b9c8598a4840ff5cf7))
* **core:** add file filtering functionality ([8864237](https://github.com/phodal/shire/commit/88642371187671d02f3fc95335a8b1fd88073d88))
* **core:** add Java build system provider ([5afd1ba](https://github.com/phodal/shire/commit/5afd1ba4c2c81722a488f454130d13fca996f2c4))
* **core:** add ProjectRunService interface and extension point ([71d02e1](https://github.com/phodal/shire/commit/71d02e1fa5e7f5ed01c243f02fd68859dd5485b2))
* **core:** add ToolchainProvider extension point ([6a6d1d8](https://github.com/phodal/shire/commit/6a6d1d8a679e15694618b5043311714027dda14d))
* **docs:** add roadmap section to README ([e2def4d](https://github.com/phodal/shire/commit/e2def4d965b5dace4c3978dce269426df39dd401))
* **docs:** add support for frontmatter in code highlighting ([dbf5765](https://github.com/phodal/shire/commit/dbf5765e5eb9b4b76f2b27e625065af063dfb78f))
* **frontmatter:** add filename and file content filters ([be33598](https://github.com/phodal/shire/commit/be3359814375937a125fbe25efd7f01582dfc8c0))
* **git:** add GitQuery and VcsPrompting classes ([f5d5fe7](https://github.com/phodal/shire/commit/f5d5fe743edcef610e14b5ffb902856035faecb7))
* **git:** add GitQuery and VcsPrompting classes ([eddfe37](https://github.com/phodal/shire/commit/eddfe372741acc6fac73c39f78ad9b22763806cf))
* **grammar:** add qualRefExpr to ShireParser.bnf ([e5bf261](https://github.com/phodal/shire/commit/e5bf26123704d8ade8f2302925aa60cb18854a82))
* **grammar:** add support for 'when' keyword in condition expressions ([ee3dc7e](https://github.com/phodal/shire/commit/ee3dc7ee33568d8265bfc07d368c9ee6a5148930))
* **grammar:** add support for pattern actions ([9471994](https://github.com/phodal/shire/commit/947199424eb5f6c5354d278500c65e7dfa132343))
* **grammar:** extend grammar for expressions and operators ([0aa661f](https://github.com/phodal/shire/commit/0aa661fc31934df8661f288c2efbbc300c7a5a6d))
* **hobbit:** add support for action location in HobbitHole ([3c530dd](https://github.com/phodal/shire/commit/3c530ddd20b938454e77edc22c069e2d1aec6dea))
* **hobbit:** add support for selection strategy ([0b4e090](https://github.com/phodal/shire/commit/0b4e090472f418023342460f4905bdcba9fb3326))
* **httpclient:** add HttpClientRunService for HTTP requests ([8a33137](https://github.com/phodal/shire/commit/8a33137a00783c4c110b00e810f8a08b1925be9e))
* **httpclient:** add support for REST client plugin ([994ff0e](https://github.com/phodal/shire/commit/994ff0e142419d33951036e9bce3300b57b94fc8))
* **index:** add ShireIdentifierIndex for file content ([62891a0](https://github.com/phodal/shire/commit/62891a0d0c5ffc8888515ff2471fdcab41c6b930))
* **intention:** add Shire Assistant with AI AutoAction ([0b23d35](https://github.com/phodal/shire/commit/0b23d3584dbb87b193aedd14a8d93bd4288051f5))
* **intention:** add Shire Hobbit AI action support ([c1122e1](https://github.com/phodal/shire/commit/c1122e182d5deb5da52f061177e5f1a34f60511d))
* **java:** add Java element strategy builder ([a4d571a](https://github.com/phodal/shire/commit/a4d571ac2a3b6bb41cb6af23c80955ff7d1e1b46))
* **java:** add Java symbol provider implementation ([76c2042](https://github.com/phodal/shire/commit/76c2042268e2e5f92b7ff261dbe8c793269891ed))
* **java:** add Java toolchain provider ([864c1cb](https://github.com/phodal/shire/commit/864c1cb6ca6af4088dcdbb586954af8d72174f70))
* **java:** add method to find nearest target element ([3cf769c](https://github.com/phodal/shire/commit/3cf769c0c41efd8fe001cce91e5bc4a97e3d1df3))
* **java:** add method to get run task name ([4e4ab8a](https://github.com/phodal/shire/commit/4e4ab8a1b107a0d0eeb5c02dbd67d39272affaab))
* **java:** add MvcContextService and ControllerContext ([b5edce5](https://github.com/phodal/shire/commit/b5edce576ef24eae8892a8e1b3b09ff1acfa8b91))
* **language:** add Shire language injector, folding builder, and completion contributor ([8f5ca99](https://github.com/phodal/shire/commit/8f5ca99d51554dd0d93da3a62fe50e9ee8324bef))
* **language:** add Shire language support ([8332a3c](https://github.com/phodal/shire/commit/8332a3c93dd0c922e118694089264b68d3d312a8))
* **language:** add Shire language support ([f351552](https://github.com/phodal/shire/commit/f3515520c86f99c8b0387614f86cc93a2e87bfa5))
* **llm:** add MockProvider for testing ([089d496](https://github.com/phodal/shire/commit/089d4968d9fa6c44314a0f21c393de96808ce863))
* **llm:** add OpenAI LLM provider and settings ([8518bd3](https://github.com/phodal/shire/commit/8518bd3360b2dd1d40bc688c3409419b4acc4aa6))
* **middleware:** add CodeVerifyProcessor for syntax error checking ([1bec4c3](https://github.com/phodal/shire/commit/1bec4c33f07be7cbc3f516e4ce57cb216d9b76d1))
* **middleware:** add post processor support ([0a2aeea](https://github.com/phodal/shire/commit/0a2aeeaf5ec9407cbf95620a3303373e3a886beb))
* **middleware:** add PostCodeHandler interface and PostCodeHandle enum ([9d734c5](https://github.com/phodal/shire/commit/9d734c54d475a887df34ebb2ade8a429857a1d28))
* **middleware:** add TimeMetricProcessor for measuring code execution time ([2c3b1a6](https://github.com/phodal/shire/commit/2c3b1a6c9589aea733666bc533dc130747fea5da))
* **modify:** add ShireModificationListener for file modification ([c61f973](https://github.com/phodal/shire/commit/c61f97347945ed6d2610608289c74537909bdf43))
* **modify:** add ShireModificationListener for file modification ([230b00e](https://github.com/phodal/shire/commit/230b00e9d0b36c82a36da3b3bf861845faeb439c))
* **parser:** add new test data files and refactor code ([b666fc6](https://github.com/phodal/shire/commit/b666fc6d3a9d51ab17972082227eeee6a5ba32d6))
* **parser:** add support for front matter parsing ([01ef64e](https://github.com/phodal/shire/commit/01ef64ebaed4951e32892b8d0bf0704b0974c994))
* **parser:** add support for pattern actions ([bb30004](https://github.com/phodal/shire/commit/bb30004e14684c9127eb138167336e5b4fd93d87))
* **parser:** add support for pattern element ([12fb06e](https://github.com/phodal/shire/commit/12fb06e83338c143b8614a0ad27485c1c0c1d2c7))
* **pattern:** add pattern actions for filtering, sorting, and executing commands ([be08b28](https://github.com/phodal/shire/commit/be08b28353dfbd940c3c585bd1a2f1fe1f7563f6))
* **project:** add core and language modules ([2b4a6f0](https://github.com/phodal/shire/commit/2b4a6f06733149d0cd9763e2d4719a048fa37ce3))
* **provider:** add AutoTesting provider for unit testing ([2fb10e2](https://github.com/phodal/shire/commit/2fb10e26c27dbd4d2d2f2090c84827dae5a37680))
* **provider:** add PsiElementStrategyBuilder interface ([c5ca72b](https://github.com/phodal/shire/commit/c5ca72b86b4fa84417bf7646500b1cb4ddf52da4))
* **provider:** add RevisionProvider interface and implementation ([fa05221](https://github.com/phodal/shire/commit/fa05221654c53c7821e5de8ff148d62c6c0a9b3c))
* **provider:** add RunService interface and implementation ([a509717](https://github.com/phodal/shire/commit/a5097170adb1efc848aa027761613b213f0c39b8))
* **psi:** add method to get relative PsiElement with PsiComment ([ba3b521](https://github.com/phodal/shire/commit/ba3b5210647242802c6c66b0fb968344345f674c))
* **run:** add Shire program runner and process processor ([554c39f](https://github.com/phodal/shire/commit/554c39f8784773a53063e5eab9246e4b4d87a8aa))
* **runner:** add support for running tasks in projects ([586a9d6](https://github.com/phodal/shire/commit/586a9d6bcfc5979fd12d5b2089eb339ae03238f5))
* **settings:** add LlmCoroutineScope, CustomAgent loadFromProject, and TestConnection ([efdc2f9](https://github.com/phodal/shire/commit/efdc2f99ff0739edb0a9d40a62404a68defece37))
* **settings:** add Shire settings configurable UI ([dba5d68](https://github.com/phodal/shire/commit/dba5d68ec0f969ebce518ab573d3471de3b2fe10))
* **shell:** add shell language support plugin file ([bdc1c90](https://github.com/phodal/shire/commit/bdc1c90f10d7c2c4fb8f7157db343dd756c11352))
* **shire:** add Shire context action group and location support ([e3df86d](https://github.com/phodal/shire/commit/e3df86d16d0c9985fec787a8e76ce553be9a9a45))

[Unreleased]: https://github.com/phodal/shire/compare/v1.3.4...HEAD
[1.3.4]: https://github.com/phodal/shire/compare/v1.3.3...v1.3.4
[1.3.3]: https://github.com/phodal/shire/compare/v1.3.2...v1.3.3
[1.3.2]: https://github.com/phodal/shire/compare/v1.3.1...v1.3.2
[1.3.1]: https://github.com/phodal/shire/compare/v1.2.4...v1.3.1
[1.2.4]: https://github.com/phodal/shire/compare/v1.2.3...v1.2.4
[1.2.3]: https://github.com/phodal/shire/compare/v1.2.2...v1.2.3
[1.2.2]: https://github.com/phodal/shire/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/phodal/shire/compare/v1.1.1...v1.2.1
[1.1.1]: https://github.com/phodal/shire/compare/v1.0.6...v1.1.1
[1.0.6]: https://github.com/phodal/shire/compare/v1.0.4-SNAPSHOT...v1.0.6
[1.0.4-SNAPSHOT]: https://github.com/phodal/shire/compare/v1.0.2...v1.0.4-SNAPSHOT
[1.0.2]: https://github.com/phodal/shire/compare/v0.9.1...v1.0.2
[0.9.1]: https://github.com/phodal/shire/compare/v0.8.2...v0.9.1
[0.8.2]: https://github.com/phodal/shire/compare/v0.8.1...v0.8.2
[0.8.1]: https://github.com/phodal/shire/compare/v0.7.4...v0.8.1
[0.7.4]: https://github.com/phodal/shire/compare/v0.7.2...v0.7.4
[0.7.2]: https://github.com/phodal/shire/compare/v0.7.1...v0.7.2
[0.7.1]: https://github.com/phodal/shire/compare/v0.5.2...v0.7.1
[0.5.2]: https://github.com/phodal/shire/compare/v0.4.8...v0.5.2
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
