-- Seed supported locales
INSERT INTO languages (id, code, name)
VALUES
    (gen_random_uuid(), 'en-US', 'English (United States)'),
    (gen_random_uuid(), 'en-CA', 'English (Canada)'),
    (gen_random_uuid(), 'fr-CA', 'French (Canada)'),
    (gen_random_uuid(), 'fr-FR', 'French (France)'),
    (gen_random_uuid(), 'es-US', 'Spanish (United States)'),
    (gen_random_uuid(), 'es-ES', 'Spanish (Spain)'),
    (gen_random_uuid(), 'de-DE', 'German (Germany)'),
    (gen_random_uuid(), 'it-IT', 'Italian (Italy)'),
    (gen_random_uuid(), 'pt-BR', 'Portuguese (Brazil)'),
    (gen_random_uuid(), 'pt-PT', 'Portuguese (Portugal)'),
    (gen_random_uuid(), 'nl-NL', 'Dutch (Netherlands)'),
    (gen_random_uuid(), 'pl-PL', 'Polish (Poland)'),
    (gen_random_uuid(), 'ru-RU', 'Russian (Russia)'),
    (gen_random_uuid(), 'uk-UA', 'Ukrainian (Ukraine)'),
    (gen_random_uuid(), 'zh-CN', 'Chinese (Simplified, China)'),
    (gen_random_uuid(), 'zh-TW', 'Chinese (Traditional, Taiwan)'),
    (gen_random_uuid(), 'ja-JP', 'Japanese (Japan)'),
    (gen_random_uuid(), 'ko-KR', 'Korean (South Korea)'),
    (gen_random_uuid(), 'hi-IN', 'Hindi (India)'),
    (gen_random_uuid(), 'ar-SA', 'Arabic (Saudi Arabia)'),
    (gen_random_uuid(), 'tr-TR', 'Turkish (Turkey)');

