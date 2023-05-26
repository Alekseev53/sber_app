#require: slotfilling/slotFilling.sc
require: common.js
  module = sys.zb-common

# Подключение javascript обработчиков
require: js/actions.js

# Подключение сценарных файлов
#require: sc/addNote.sc

patterns:
    $AnyText = $nonEmptyGarbage
    $Num1 = $nonEmptyGarbage
    $Num2 = $nonEmptyGarbage

theme: /
    state: Start
        q!: * *start
        q!: * (~помощь/~справка/помоги*/help/хелп/~меню/може*/умее*) *
        script:
            $jsapi.startSession();

    state: Fallback
        event!: noMatch
        a: Я не понимаю, вы сказали: {{request.query}}

theme: /
    state: Настройки
        q!: * (настройк*/настрои*/режим*) *
        script:
            #открываем страницу с настройками
        
    state: АрифметикаДобавить
        q!: * (включи*/добав*) 
            $AnyText::anyText
        if: $parseTree._anyText == "сложение" || "вычитание" || "умножение" || "деление" || "плюс" || "минус" || "произведение" || "частное"
            a: Операция {{$parseTree._anyText}} добавлена
            script:
                addOperation($parseTree._anyText, $context);
        else: 
            a: Такой операции нет, идите нахуй
            
    state: АрифметикаУбрать
        q!: * (убрать/отключи*/убер*) 
            $AnyText::anyText
        a: Операция {{$parseTree._anyText}} отключена
        script:
            dropOperation($parseTree._anyText, $context);
        
    state: КоличествоОпераций
        q!: * (количество операций) * 
            $AnyText::anyText
        a: Количество операций {{$parseTree._anyText}} установлено
        script:
            setOperationsCount($parseTree._anyText, $context);
        
    state: ДиапазонПримера
        q!: [выб*] * (диапазо*) [чисел/примера/в примере] (от) $Num1::num1 (до) $Num2::num2
        a: Диапазон чисел от {{$parseTree._num1}} до {{$parseTree._num2}} установлен
        script:
            setOperationsRange($parseTree._num1, $parseTree._num2, $context);
        
    state: ЗаданиеВыполнено
        event!: done
        event!: DONE
        random: 
            a: Молодец!
            a: Красавчик!
            a: Супер!
            
    state: Пример
        q!: (~дай задачу/~дай пример/старт/дальше/далее/следующ*/еще/~другой) 
            [~уравнение|~пример|~задание|~задача]
            
        script:
            generateExample($context);
            
        a: 2 + 2 = 
        q!: [* будет/* это/* равно] $AnyText::anyText
        if: $parseTree._anyText == "4"
            random: 
                a: Молодец!
                a: Красавчик!
                a: Супер!
        else:
            a: Ты проебался, дружок