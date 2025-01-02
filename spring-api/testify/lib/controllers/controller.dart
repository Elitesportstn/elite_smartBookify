        import 'dart:convert';
        import 'dart:io';
        import 'package:flutter/services.dart';
        import 'package:flutter_tts/flutter_tts.dart';
        import 'package:get/get.dart';
        import 'package:flutter/material.dart';
        import 'package:pdfx/pdfx.dart';
        import 'package:http/http.dart' as http;
        import 'package:syncfusion_flutter_pdf/pdf.dart' as syncfusion;
        import 'package:path_provider/path_provider.dart';

        class PdfViewerController extends GetxController
            with SingleGetTickerProviderMixin {
          late AnimationController foldAnimationController;
          var currentPage = 1.obs;
          var totalPages = 0.obs;
          var isLoading = true.obs;
          var loadingError = false.obs;
          late PdfController? pdfController;
          late FlutterTts flutterTts;
          final String pdfPath = 'assets/pdf/file.pdf'; // URL of the PDF à charger
          late AnimationController animationController;
          late Animation<double> foldAnimation;
          File? downloadedPdfFile;
          String extractedText = '';
          String translatedText = '';
          var currentIndex = 0.obs;
          var audioLanguage = 'en'.obs;
          var translationLanguage = 'en'.obs;
          var sumText = ''.obs;
          var pdfUrl = '';

          var isTranslated = false.obs;

          var isSpeaking = false.obs;

          var isSummarized = false.obs;

          @override
          onInit() async {
            super.onInit();
            animationController = AnimationController(
              duration: const Duration(milliseconds: 700),
              vsync: this,
            );
            foldAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
              CurvedAnimation(parent: animationController, curve: Curves.easeInOut),
            );
            flutterTts = FlutterTts();

            flutterTts.completionHandler = () {
              isSpeaking.value = false;
            };
            _initializePdf();
            getFileFromAssets();
          }

          getFileFromAssets() async {
            // Load the asset data as bytes
            final byteData = await rootBundle.load('assets/pdf/file.pdf');

            // Get a temporary directory
            final tempDir = await getTemporaryDirectory();

            // Create a file in the temporary directory
            final file = File('${tempDir.path}/file.pdf');

            // Write the bytes to the file
            await file.writeAsBytes(byteData.buffer.asUint8List());

            downloadedPdfFile = file;
            print(downloadedPdfFile?.path);
          }

          Future<void> _initializePdf() async {
            try {
              final pdfDocument = PdfDocument.openAsset(pdfPath);
              pdfController = PdfController(
                document: pdfDocument,
                initialPage: 1,
              );

              isLoading.value = false;
              loadingError.value = false;
            } catch (e) {
              loadingError.value = true;
              isLoading.value = false;
              print(e);
              Get.snackbar(
                " failed",
                " failed : ${e}.",
                snackPosition: SnackPosition.BOTTOM,
              );
            }
          }

          void goToPage(int page) {
            if (currentPage.value != page) {
              currentPage.value = page;
              pdfController?.animateToPage(
                page,
                duration: const Duration(milliseconds: 500),
                curve: Curves.ease,
              );
              animationController.forward(from: 0.0);
              loadTextFromPdf(); // Charger le texte après navigation
            }
          }

          void onStopPressed() {
            flutterTts.stop();
            isSpeaking.value = false;
          }

          Future<String> fetchTranslation(String text, String audioLanguage) async {
            isLoading.value = true;
            String targetLang = audioLanguage;
            const String apiUrl = 'https://sd3.savooria.com/translate';

            final Map<String, dynamic> body = {
              'text': text,
              'targetLang': targetLang,
            };

            final response = await http.post(
              Uri.parse(apiUrl),
              headers: {'Content-Type': 'application/json'},
              body: json.encode(body),
            );
            if (response.statusCode == 200) {
              final Map<String, dynamic> responseData = json.decode(response.body);
              isLoading.value = false;
              return responseData['translatedText'] ?? 'Translation not available';
            } else {
              isLoading.value = false;
              return 'Failed to load translation';
            }
          }

          loadTextFromPdf() {
            if (downloadedPdfFile == null) {
              return;
            }

            try {
              final document = syncfusion.PdfDocument(
                  inputBytes: downloadedPdfFile!.readAsBytesSync());
              extractedText = syncfusion.PdfTextExtractor(document)
                  .extractText(startPageIndex: currentPage.value - 1);
              document.dispose();
            } catch (e) {
              Get.snackbar(
                " Error",
                " Error : ${e}.",
                snackPosition: SnackPosition.BOTTOM,
              );
            }
            return extractedText;
          }

          @override
          void onClose() {
            super.onClose();
            flutterTts.stop();
            animationController.dispose();
            pdfController?.dispose();
          }

          void summarize() async {
            isLoading.value = true;
            isSummarized.value = true;
            try {
              var requestBody = json.encode({
                "file_url": pdfUrl,
                "question": 'Summarize this File',
              });
              var summarizeResponse = await http.post(
                Uri.parse('https://sd2.savooria.com/summarize'),
                headers: {'Content-Type': 'application/json'},
                body: requestBody,
              );
              isLoading.value = false;

              sumText.value = jsonDecode(summarizeResponse.body)["summary"];
            } catch (e) {
              Get.snackbar(
                " Error",
                " Error : ${e}.",
                snackPosition: SnackPosition.BOTTOM,
              );
            }
          }

          translate() async {
            isTranslated.value = false;
            if (!isLoading.value) {
              isLoading.value = true;
              const apiUrl = 'https://sd3.savooria.com/translate';

              var body = {
                'text': cleanExtractedText(loadTextFromPdf()),
                'targetLang': translationLanguage.value,
              };
              final response = await http.post(
                Uri.parse(apiUrl),
                headers: {'Content-Type': 'application/json'},
                body: json.encode(body),
              );
              if (response.statusCode == 200) {
                isLoading.value = false;
                isTranslated.value = true;
                var responseData = json.decode(response.body);
                translatedText = responseData["translatedText"];
                isTranslated.value = true;
                return responseData;
              } else {
                isLoading.value = false;
                isTranslated.value = true;

                return 'Failed to load translation';
              }
            }
          }

          String cleanExtractedText(String text) {
            return text
                .replaceAll(
                    RegExp(r'^\s*$\n', multiLine: true), '') // Remove empty lines
                .replaceAll(
                    RegExp(r'^\d+\.\s*', multiLine: true), '') // Remove numbered lists
                .replaceAll(RegExp(r'\. '), '.\n'); // Add a newline after each period
          }

          void speak() async {
            switch (audioLanguage.value) {
              case 'en':
                flutterTts.setLanguage('es-US');
              case 'ar':
                flutterTts.setLanguage('ar');
              case 'fr':
                flutterTts.setLanguage('fr-CA');
              case 'es':
                flutterTts.setLanguage('es-ES');
                break;
              default:
            }
            flutterTts.stop();
            var txtToRead =
                await fetchTranslation(loadTextFromPdf(), audioLanguage.value);
            flutterTts.speak(txtToRead);
            isSpeaking.value = true;
          }

          final TextEditingController messageController = TextEditingController();
          ScrollController chatScrollerController = ScrollController();
          var chat = ChatRequest(messages: [
            chatMessage(role: 'system', content: "Hi! How can i assist you ?")
          ], model: "llama2")
              .obs;
          var isProcessing = false.obs;

          sendMessage() async {
            final message = messageController.text;
            chat.value.messages!.add(chatMessage(role: "user", content: message));
            chatScrollerController.animateTo(
                curve: Curves.decelerate,
                chatScrollerController.position.maxScrollExtent,
                duration: const Duration(seconds: 1));

            messageController.clear();
            isProcessing.value = true;

            var body = chat.value.toJson();

            final response = await http.post(Uri.parse('https://sd5.savooria.com/chat'),
                headers: {'Content-type': 'application/json'}, body: jsonEncode(body));
            print(response.body);
            chatScrollerController.animateTo(
                chatScrollerController.position.maxScrollExtent +
                    chatScrollerController.offset,
                curve: Curves.easeOut,
                duration: const Duration(microseconds: 500));
            if (response.statusCode == 200) {
              isProcessing.value = false;
              var callResult = jsonDecode(response.body);
              chat.value.messages!.add(chatMessage(
                  role: "system", content: callResult["message"]["content"]));
              print(chat.value.messages!);
            } else {
              print(response.statusCode);
              print(response.body.toString());

              isProcessing.value = false;
              throw Exception('failed'.tr);
            }
          }
        }

        class ChatRequest {
          List<chatMessage>? messages;
          String? model;

          ChatRequest({this.messages, this.model});

          ChatRequest.fromJson(Map<String, dynamic> json) {
            if (json['messages'] != null) {
              messages = <chatMessage>[];
              json['messages'].forEach((v) {
                messages!.add(chatMessage.fromJson(v));
              });
            }
            model = json['model'];
          }

          Map<String, dynamic> toJson() {
            final Map<String, dynamic> data = <String, dynamic>{};
            if (messages != null) {
              data['messages'] = messages!.map((v) => v.toJson()).toList();
            }
            data['model'] = model;
            return data;
          }
        }

        class chatMessage {
          String? role;
          String? content;

          chatMessage({this.role, this.content});

          chatMessage.fromJson(Map<String, dynamic> json) {
            role = json['role'];
            content = json['content'];
          }

          Map<String, dynamic> toJson() {
            final Map<String, dynamic> data = <String, dynamic>{};
            data['role'] = role;
            data['content'] = content;
            return data;
          }
        }
