        import 'package:flutter/material.dart';
                import 'package:get/get.dart';
                import 'dart:math' as math;
                import 'package:iconsax/iconsax.dart';
                import 'package:pdfx/pdfx.dart';
                import '../controllers/controller.dart';

        class LoginPage extends StatefulWidget {
          @override
          _LoginPageState createState() => _LoginPageState();
        }

        class _LoginPageState extends State<LoginPage> {
          final TextEditingController _emailController = TextEditingController();
          final TextEditingController _passwordController = TextEditingController();

          @override
          Widget build(BuildContext context) {
            return Scaffold(
              body: SafeArea(
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Text(
                        'Welcome Back!',
                        style: TextStyle(
                          fontSize: 32,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                      SizedBox(height: 20),
                      Text(
                        'Sign in to continue',
                        style: TextStyle(
                          fontSize: 16,
                          color: Colors.grey[600],
                        ),
                      ),
                      SizedBox(height: 40),
                      TextField(
                        controller: _emailController,
                        decoration: InputDecoration(
                          hintText: 'Email',
                          prefixIcon: Icon(Icons.email),
                          border: OutlineInputBorder(),
                          contentPadding:
                              EdgeInsets.symmetric(vertical: 15, horizontal: 20),
                        ),
                        keyboardType: TextInputType.emailAddress,
                      ),
                      SizedBox(height: 20),
                      TextField(
                        controller: _passwordController,
                        decoration: InputDecoration(
                          hintText: 'Password',
                          prefixIcon: Icon(Icons.lock),
                          border: OutlineInputBorder(),
                          contentPadding:
                              EdgeInsets.symmetric(vertical: 15, horizontal: 20),
                        ),
                        obscureText: true,
                      ),
                      SizedBox(height: 20),
                      ElevatedButton(
                        onPressed: () {
                          Get.to(() => PdfMainScreen());
                        },
                        style: ElevatedButton.styleFrom(
                          minimumSize: Size(double.infinity, 50),
                          backgroundColor: Colors.blue,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(30),
                          ),
                        ),
                        child: Text(
                          'Log In',
                          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                        ),
                      ),
                      SizedBox(height: 20),
                      GestureDetector(
                        onTap: () {
                          // Navigate to Sign Up or Forgot Password screen
                        },
                        child: Text(
                          'Forgot Password?',
                          style: TextStyle(color: Colors.blue),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          }
        }

        class PdfMainScreen extends StatefulWidget {
          const PdfMainScreen({
            Key? key,
          }) : super(key: key);

          @override
          _PdfMainScreenState createState() => _PdfMainScreenState();
        }

        class _PdfMainScreenState extends State<PdfMainScreen>
            with SingleTickerProviderStateMixin {
          final List<Widget> pages = [
            Center(child: Text('Search Page')),
            Center(child: Text('Profile Page')),
          ];
          final bottom_items = [
            BottomNavigationBarItem(
              icon: const Icon(Iconsax.book),
              activeIcon: const Icon(Iconsax.book, color: Colors.blueGrey),
              label: 'Summary'.tr,
              tooltip: "Summary Page".tr,
            ),
            BottomNavigationBarItem(
              icon: const Icon(Iconsax.translate),
              activeIcon: const Icon(Iconsax.translate, color: Colors.blueGrey),
              label: 'Translating'.tr,
              tooltip: "Translating Page".tr,
            ),
            BottomNavigationBarItem(
              icon: const Icon(Iconsax.message),
              activeIcon: const Icon(Iconsax.message, color: Colors.blueGrey),
              label: 'Chatting'.tr,
              tooltip: "Chatting Page".tr,
            ),
            BottomNavigationBarItem(
              icon: const Icon(Iconsax.headphone),
              activeIcon: const Icon(Iconsax.headphone, color: Colors.blueGrey),
              label: 'Listening'.tr,
              tooltip: "Listening Page".tr,
            )
          ];

          late PdfViewerController controller;

          @override
          void initState() {
            super.initState();
            controller = Get.put(PdfViewerController());
          }

          @override
          void dispose() {
            controller.dispose();
            super.dispose();
          }

          @override
          Widget build(BuildContext context) {
            return Scaffold(
                appBar: AppBar(
                  title: Obx(
                      () => Text(bottom_items[controller.currentIndex.value].label!)),
                  actions: [
                    Obx(() => controller.currentIndex.value != 0 &&
                            controller.currentIndex.value != 2
                        ? PopupMenuButton<String>(
                            icon: const Icon(Iconsax.language_circle),
                            onSelected: (value) {
                              handleSelectedSetting(value);
                            },
                            itemBuilder: (BuildContext context) => getSettingsItems())
                        : const Center()),
                    Obx(() {
                      if (controller.currentIndex.value == 3) {
                        return IconButton(
                          onPressed: controller.isSpeaking.value
                              ? controller.onStopPressed
                              : controller.speak,
                          icon: Icon(controller.isSpeaking.value
                              ? Iconsax.stop
                              : Iconsax.play),
                        );
                      }
                      if (controller.currentIndex.value == 1) {
                        return IconButton(
                          onPressed: () => controller.translate(),
                          icon: const Icon(Iconsax.translate),
                        );
                      }
                      if (controller.currentIndex.value == 0) {
                        return IconButton(
                          onPressed: () => controller.summarize(),
                          icon: const Icon(Iconsax.book),
                        );
                      }
                      return const SizedBox
                          .shrink(); // Returns an empty widget instead of Center()
                    }),
                  ],
                ),
                body: Obx(() {
                  switch (controller.currentIndex.value) {
                    case 0: //summury
                      return controller.isSummarized.value
                          ? SummarizedView()
                          : MainView();
                    case 1: //translating
                      return controller.isTranslated.value
                          ? TranslatedView()
                          : MainView();
                    case 2: //  chatting
                      return chatUi();
                    case 3: //listening
                      return ListeningView();
                    default:
                      return MainView();
                  }
                }),
                bottomNavigationBar: Obx(
                  () => BottomNavigationBar(
                    currentIndex: controller.currentIndex.value,
                    selectedItemColor: Colors.blueGrey,
                    unselectedItemColor: Colors.grey[400],
                    selectedLabelStyle: const TextStyle(fontWeight: FontWeight.w600),
                    onTap: (i) => controller.currentIndex.value = i,
                    items: bottom_items,
                  ),
                ));
          }

          TranslatedView() {
            return SingleChildScrollView(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 24.0),
                child: Text(
                  controller.translatedText,
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w500,
                    fontFamily: 'Georgia',
                    color: Colors.black,
                    letterSpacing: -0.5,
                    height: 1.2,
                  ),
                ),
              ),
            );
          }

          SummarizedView() {
            return controller.isLoading.value
                ? Center(
                    child: CircularProgressIndicator(),
                  )
                : SingleChildScrollView(
                    child: Padding(
                      padding:
                          const EdgeInsets.symmetric(horizontal: 16.0, vertical: 24.0),
                      child: Center(
                        child: Text(
                          textAlign: TextAlign.center,
                          controller.sumText.value,
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w500,
                            fontFamily: 'Georgia',
                            color: Colors.black,
                            letterSpacing: -0.5,
                            height: 1.2,
                          ),
                        ),
                      ),
                    ),
                  );
          }

          MainView() {
            return Obx(() {
              if (controller.isLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(),
                );
              } else if (controller.pdfController == null) {
                return const Center(
                  child: Text('PDF non initialisé'),
                );
              } else {
                return SizedBox(
                    height: Get.height * 0.9,
                    width: Get.width,
                    child: Stack(
                      children: [
                        AnimatedBuilder(
                          animation: controller.foldAnimation,
                          builder: (context, child) {
                            final isFoldingPage = controller.foldAnimation.value < 1;
                            final rotationAngle = isFoldingPage
                                ? controller.foldAnimation.value * (math.pi / 2)
                                : (1 - controller.foldAnimation.value) * (math.pi / 2);

                            return Transform(
                              alignment: Alignment.centerLeft,
                              transform: Matrix4.identity()
                                ..setEntry(3, 2, 0.001)
                                ..rotateY(rotationAngle),
                              child: PdfView(
                                key: ValueKey<int>(controller.currentPage.value),
                                controller: controller.pdfController!,
                                scrollDirection: Axis.horizontal,
                                onPageChanged: (page) {
                                  controller.goToPage(page);
                                },
                              ),
                            );
                          },
                        ),
                      ],
                    ));
              }
            });
          }

          getSettingsItems() {
            switch (controller.currentIndex.value) {
              case 0:
                return [];
              case 1:
                return [
                  const PopupMenuItem<String>(
                    value: 'translate_to_en',
                    child: Text('English'),
                  ),
                  const PopupMenuItem<String>(
                    value: 'translate_to_fr',
                    child: Text('French'),
                  ),
                  const PopupMenuItem<String>(
                    value: 'translate_to_ar',
                    child: Text('Arabic'),
                  ),
                  const PopupMenuItem<String>(
                    value: 'translate_to_sp',
                    child: Text('Spanish'),
                  )
                ];
              case 3:
                return [
                  const PopupMenuItem<String>(
                    value: 'listen_to_en',
                    child: Text('English'),
                  ),
                  const PopupMenuItem<String>(
                    value: 'listen_to_fr',
                    child: Text('French'),
                  ),
                  const PopupMenuItem<String>(
                    value: 'listen_to_ar',
                    child: Text('Arabic'),
                  ),
                  const PopupMenuItem<String>(
                    value: 'listen_to_sp',
                    child: Text('Spanish'),
                  )
                ];

              default:
            }
          }

          handleSelectedSetting(String value) {
            switch (value) {
              case 'listen_to_en':
                controller.audioLanguage.value = 'en';
              case 'listen_to_ar':
                controller.audioLanguage.value = 'ar';
              case 'listen_to_fr':
                controller.audioLanguage.value = 'fr';
              case 'listen_to_sp':
                controller.audioLanguage.value = 'es';

              case 'translate_to_en':
                controller.translationLanguage.value = 'en';
              case 'translate_to_ar':
                controller.translationLanguage.value = 'ar';
              case 'translate_to_fr':
                controller.translationLanguage.value = 'fr';
              case 'translate_to_sp':
                controller.translationLanguage.value = 'es';
                break;
              default:
            }
          }

          ListeningView() {
            return Obx(() {
              if (controller.isLoading.value) {
                return const Center(
                  child: CircularProgressIndicator(),
                );
              } else if (controller.pdfController == null) {
                return const Center(
                  child: Text('PDF non initialisé'),
                );
              } else {
                return SizedBox(
                    height: Get.height * 0.9,
                    width: Get.width,
                    child: Stack(
                      children: [
                        AnimatedBuilder(
                          animation: controller.foldAnimation,
                          builder: (context, child) {
                            final isFoldingPage = controller.foldAnimation.value < 1;
                            final rotationAngle = isFoldingPage
                                ? controller.foldAnimation.value * (math.pi / 2)
                                : (1 - controller.foldAnimation.value) * (math.pi / 2);

                            return Transform(
                              alignment: Alignment.centerLeft,
                              transform: Matrix4.identity()
                                ..setEntry(3, 2, 0.001)
                                ..rotateY(rotationAngle),
                              child: PdfView(
                                key: ValueKey<int>(controller.currentPage.value),
                                controller: controller.pdfController!,
                                scrollDirection: Axis.horizontal,
                                onPageChanged: (page) {
                                  controller.goToPage(page);
                                },
                              ),
                            );
                          },
                        ),
                      ],
                    ));
              }
            });
          }
        }

        chatUi() {
          var chatcontroller = Get.put(PdfViewerController());
          return Obx(
            () => Column(
              children: [
                Expanded(
                  child: ListView.builder(
                    controller: chatcontroller.chatScrollerController,
                    itemCount: chatcontroller.chat.value.messages!.length,
                    itemBuilder: (context, index) {
                      return _buildMessageStack(
                          chatcontroller.chat.value.messages![index]);
                    },
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          controller: chatcontroller.messageController,
                          decoration:
                              InputDecoration(hintText: 'Write a message...'.tr),
                        ),
                      ),
                      chatcontroller.isProcessing.value
                          ? const Center(
                              child: CircularProgressIndicator(
                              color: Colors.black,
                            ))
                          : IconButton(
                              icon: const Icon(Icons.send),
                              onPressed: () {
                                chatcontroller.sendMessage();
                              },
                            ),
                    ],
                  ),
                ),
              ],
            ),
          );
        }

        Widget _buildMessageStack(dynamic message) {
          return Container(
            padding: const EdgeInsets.all(10.0),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(10.0),
            ),
            child: Stack(children: [
              Positioned(
                  bottom: 0,
                  right: 0,
                  child: message.role == "user"
                      ? Container(
                          width: Get.width * 0.1,
                          height: Get.width * 0.1,
                          clipBehavior: Clip.antiAlias,
                          decoration: const BoxDecoration(
                            shape: BoxShape.circle,
                          ),
                          child: Image.asset(
                            "assets/images/logoo.png",
                            fit: BoxFit.cover,
                          ),
                        )
                      : SizedBox(
                          width: Get.width * 0.1,
                          height: Get.width * 0.1,
                          child: Image.asset(
                            "assets/images/chatbot.png",
                          ))),
              Container(
                decoration: BoxDecoration(
                    color: message.role == "user" ? Colors.grey : Colors.white,
                    borderRadius: BorderRadius.circular(10)),
                width: Get.width * 0.8,
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(
                    message.content!,
                    // style: GoogleFonts.orbitron(
                    //   color: message.role == "user" ? Colors.white : Colors.black,
                    // ),
                  ),
                ),
              ),
            ]),
          );
        }

        Widget msgImg(dynamic msg) {
          return Positioned(
              left: msg.role == "user" ? 0 : null,
              bottom: 0,
              right: msg.role == "user" ? null : 0,
              child: msg.role == "user"
                  ? Container(
                      width: Get.width * 0.1,
                      height: Get.width * 0.1,
                      clipBehavior: Clip.antiAlias,
                      decoration: const BoxDecoration(
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        Iconsax.user,
                        size: 50,
                      ))
                  : SizedBox(
                      width: Get.width * 0.1,
                      height: Get.width * 0.1,
                      child: Image.asset(
                        "assets/images/logoo.png",
                      )));
        }

        Widget _buildMessage(dynamic message) {
          return ListTile(
            style: ListTileStyle.list,
            title: Container(
                padding: const EdgeInsets.all(10.0),
                decoration: BoxDecoration(
                  color: message.role == "user" ? Colors.grey : Colors.white,
                  borderRadius: BorderRadius.circular(10.0),
                ),
                child: Text(
                  message.content!,
                  // style: GoogleFonts.orbitron(
                  //   color: message.role == "user" ? Colors.white : Colors.black,
                  // ),
                )),
            leading: message.role == "user"
                ? const Positioned(bottom: 0, child: Icon(Icons.person))
                : null,
            trailing: message.role == "user"
                ? null
                : Positioned(bottom: 0, child: Image.asset('assets/images/logoo.png')),
          );
        }

