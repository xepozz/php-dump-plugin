# PHP Dump

![Build](https://github.com/xepozz/php-dump-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.

<!-- Plugin description -->

[Github](https://github.com/xepozz/php-dump-plugin) | [Telegram](https://t.me/jb_plugins/50) | [Donation](https://github.com/xepozz/xepozz?tab=readme-ov-file#become-a-sponsor)

## PHP Dump

Plugin analyzes edited PHP files and provides several useful tools.

Opcache Dumper:
- Dump opcodes for the current file 
- Provide "preload.php" to enhance opcache effectiveness

PHP tokens/nodes Dumper (future scope):
- Dump tokens for the current file
- Use nikic/php-parser to provide a tree-like view of programming entities


## Donation

Open-source tools can greatly improve workflows, helping developers and businesses save time and increase revenue.
Many successful projects have been built on these tools, benefiting a wide community.
However, maintaining and enhancing these resources requires continuous effort and investment.

Support from the community helps keep these projects alive and ensures they remain useful for everyone.
Donations play a key role in sustaining and improving these open-source initiatives.

Chose the best option for you to say thank you:

[<img height="28" src="https://github.githubassets.com/assets/patreon-96b15b9db4b9.svg"> Patreon](https://patreon.com/xepozz)
|
[<img height="28" src="https://github.githubassets.com/assets/buy_me_a_coffee-63ed78263f6e.svg"> Buy me a coffee](https://buymeacoffee.com/xepozz)
|
[<img height="28" src="https://boosty.to/favicon.ico"> Boosty](https://boosty.to/xepozz)

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "php-dump-plugin"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/xepozz/php-dump-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
