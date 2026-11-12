ZYLO BALANCE TRACKER — Fabric 1.21.11

WHAT IT DOES
- Press RIGHT SHIFT to open the custom balance screen.
- Watches normal (non-action-bar) server chat for lines containing words such as
  "Balance", "Bal", "Money", "Cash", "Purse", or "Wallet".
- Understands values such as $1,234, 1.5M, 2B, and 3.2 trillion.
- Saves current/previous balance and session totals in:
  .minecraft/config/zylo-balance-tracker.json

IMPORTANT
This is a client-side tracker. It does not read private server data or change your
real server balance. It can only update when the server sends a matching balance
message to your chat. If your server uses a different /bal message format, that
format may need to be added to the parser.

BUILDING THE MOD
1. Install Java 21 (Minecraft 1.21.11 requires Java 21).
2. Open this folder in IntelliJ IDEA as a Gradle project.
3. Let Gradle sync and download the Fabric dependencies.
4. Run the Gradle task: build
5. The mod JAR will be in build/libs/zylo-balance-tracker-1.0.0.jar
6. Put that JAR in .minecraft/mods alongside Fabric API.

Required in Minecraft:
- Fabric Loader for 1.21.11
- Fabric API for 1.21.11
