'use strict';

const phoneWord = "телефон"
const phonesWord = "телефоны"
const emailWord = "почту"
const emailsWord = "почты"
const andWord = "и"
const nameWord = "имя"

// returns true if completed or
// returns false if not
function compareStrings(mainString, comparingString) {
    for (let i = 0; i < Math.min(mainString.length, comparingString.length); i++) {
        if (mainString[i] !== comparingString[i]) {
            return false
        }
    }
    return mainString.length >= comparingString.length;

}

function transformPhone(phone) {
    return `+7 (${phone[0] + phone[1] + phone[2]}) ${phone[3] + phone[4] + phone[5]}-${phone[6] + phone[7]}-${phone[8] + phone[9]}`
}

function matchName(line) {
    return line.match(/[^;]*/)
}

function matchPhone(line) {
    return line.match(/^\d{10}$/)
}

function matchEmail(line) {
    return line.match(/[^; ]*/)
}

function countIndex(words, indexOfWord, startOfCommand) {
    let res = startOfCommand.length + 1
    if (startOfCommand === "") {
        res--
    }
    // number of whitespaces
    res += indexOfWord
    for (let i = 0; i < indexOfWord; i++) {
        res += words[i].length
    }
    return res
}

function createContact(line) {
    let name = line.slice(CommandsEnum.CREATE_CONTACT.startLine.length + 1)
    /*if (name === "") {
        return true
    }*/
    if (matchName(name)) {
        if (!phoneBook.has(name)) {
            phoneBook.set(name, {phones: new Set(), emails: new Set()})
        }
        return true;
    }
    return CommandsEnum.CREATE_CONTACT.startLine.length + 1
}

function findAllKeys(request, found) {
    phoneBook.forEach((value, key) => {
        let info = new Set()
        info.add(key)
        value.emails.forEach(info.add, info)
        value.phones.forEach(info.add, info)
        for (let elem of info) {
            if (elem.includes(request)) {
                found.push(key)
                break
            }
        }
    })
}

function deleteContacts(line) {
    let request = line.slice(CommandsEnum.DELETE_CONTACTS.startLine.length + 1)
    if (request === "") {
        return true
    }
    if (matchName(request)) {
        let found = []
        findAllKeys(request, found)
        for (let i = 0; i < found.length; i++) {
            phoneBook.delete(found[i])
        }
        return true
    }
    return CommandsEnum.DELETE_CONTACTS.startLine.length + 1
}

function deleteContact(line) {
    let name = line.slice(CommandsEnum.DELETE_CONTACT.startLine.length + 1)
    if (matchName(name)) {
        phoneBook.delete(name)
        return true;
    }
    return CommandsEnum.DELETE_CONTACT.startLine.length + 1
}

// return true if matched
// returns false is first word doesn't matched
// returns -1 if second word doesn't matched
function addIfCorrect(words, index, arrayForResult, comparedWord, matchFunc) {
    let resOfCompare = compareStrings(words[index], comparedWord)
    if (resOfCompare === true) {
        if (!matchFunc(words[index + 1])) {
            return -1;
        }
        arrayForResult.push(words[index + 1])
    }
    return resOfCompare
}

function parsePhoneAndEmailRequest(line, command, res) {
    let request = line.slice(command.startLine.length + 1)
    let words = request.split(" ")
    let index = 0;
    while (index + 1 < request.length) {
        let found = false
        let tryPhone = addIfCorrect(words, index, res.phones, phoneWord, matchPhone)
        if (tryPhone === true) {
            found = true
        } else {
            if (tryPhone === -1) {
                return countIndex(words, index + 1,  command.startLine)
            }
            let tryEmail = addIfCorrect(words, index, res.emails, emailWord, matchEmail)
            if (tryEmail === true) {
                found = true
            } else if (tryEmail === -1) {
                return countIndex(words, index + 1, command.startLine)
            }
        }
        if (!found) {
            break
        }
        index += 2
        if (index < words.length && words[index] === andWord) {
            index++
            continue
        }
        break
    }

    return lastWordsCheck(line, words, index, command, res)
}

function lastWordsCheck(line, words, index, command, res) {
    let lastWords = command.finishLine.split(" ")
    let lastIndex = index
    for (index; index < Math.min(words.length, lastWords.length + lastIndex); index++) {
        let resOfCompare = compareStrings(words[index], lastWords[index - lastIndex])
        if (resOfCompare !== true) {
            return countIndex(words, index, command.startLine)
        }
    }
    // not enough words
    if (index >= words.length) {
        return line.length
    }
    let indexOfRequest = countIndex(words, index, command.startLine)
    let request = line.slice(indexOfRequest)
    if (!matchName(request)) {
        return indexOfRequest
    }
    res.name = request
    return true
}

function addInfo(line) {
    let res = {phones: [], emails: [], name: ""}
    let resOfParse = parsePhoneAndEmailRequest(line, CommandsEnum.ADD_INFO, res)
    if (typeof resOfParse === "number") {
        return resOfParse
    }
    /*if (res.name === "") {
        return true
    }*/
    if (resOfParse === true && phoneBook.has(res.name)) {
        let acc = phoneBook.get(res.name)
        res.emails.forEach(acc.emails.add, acc.emails)
        res.phones.forEach(acc.phones.add, acc.phones)
        return true
    }
    return resOfParse
}

function deleteInfo(line) {
    let res = {phones: [], emails: [], name: ""}
    let resOfParse = parsePhoneAndEmailRequest(line, CommandsEnum.DELETE_INFO, res)
    if (typeof resOfParse === "number") {
        return resOfParse
    }
    /*if (res.name === "") {
        return true
    }*/
    if (resOfParse === true && phoneBook.has(res.name)) {
        let acc = phoneBook.get(res.name)
        res.emails.forEach(email => {
            acc.emails.delete(email)
        })
        res.phones.forEach(phone => {
            acc.phones.delete(phone)
        })
        return true
    }
    return resOfParse
}

function showInfo(line) {
    let request = line.slice(CommandsEnum.SHOW_INFO.startLine.length + 1)
    let words = request.split(" ")
    let possibleWords = [phonesWord, emailsWord, nameWord]
    let index = 0;
    let sequence = []
    while (index < words.length) {
        let found = false
        for (let i = 0; i < possibleWords.length; i++) {
            let resOfCompare = compareStrings(words[index], possibleWords[i])
            if (resOfCompare !== true) {
                continue
            }
            sequence.push(i)
            found = true
            break
        }
        if (found) {
            index++
            if (index < words.length) {
                if (words[index] === andWord) {
                    index += 1
                    continue
                }
                break
            }
            return line.length
        }
        break
    }
    let res = {name: ""}
    let lastWords = lastWordsCheck(line, words, index, CommandsEnum.SHOW_INFO, res)
    if (lastWords !== true) {
        return lastWords
    }
    if (res.name === "") {
        return true
    }
    let found = []
    findAllKeys(res.name, found)
    let result = []
    for (let index = 0; index < found.length; index++) {
        let name = found[index]
        let response = ""
        let elem = phoneBook.get(name)
        let emails = Array.from(elem.emails).join(',')
        let phonesArray = Array.from(elem.phones)
        for (let i = 0; i < phonesArray.length; i++) {
            phonesArray[i] = transformPhone(phonesArray[i])
        }
        let phones = phonesArray.join(',')
        for (let i = 0; i < sequence.length; i++) {
            switch (sequence[i]) {
                case 0: {
                    response += phones
                    if (i < sequence.length - 1) {
                        response += ";"
                    }
                    break
                }
                case 1: {
                    response += emails
                    if (i < sequence.length - 1) {
                        response += ";"
                    }
                    break
                }
                case 2: {
                    response += name
                    if (i < sequence.length - 1) {
                        response += ";"
                    }
                    break
                }
            }
        }
        result.push(response)
    }
    return result
}

const CommandsEnum = Object.freeze({
    CREATE_CONTACT: {startLine: "Создай контакт"},
    DELETE_CONTACTS: {startLine: "Удали контакты, где есть"},
    DELETE_CONTACT: {startLine: "Удали контакт"},
    ADD_INFO: {startLine: "Добавь", finishLine: "для контакта"},
    DELETE_INFO: {startLine: "Удали", finishLine: "для контакта"},
    SHOW_INFO: {startLine: "Покажи", finishLine: "для контактов, где есть"}
})

const arrayOfCommands = [
    CommandsEnum.CREATE_CONTACT,
    CommandsEnum.DELETE_CONTACTS,
    CommandsEnum.DELETE_CONTACT,
    CommandsEnum.ADD_INFO,
    CommandsEnum.DELETE_INFO,
    CommandsEnum.SHOW_INFO
]

function parse(line, numOfLine) {
    let command = null
    let syntaxErrorIndex = -1
    let words = line.split(" ")
    for (let i = 0; i < arrayOfCommands.length; i++) {
        let startWords = arrayOfCommands[i].startLine.split(" ")
        let passed = true
        for (let j = 0; j < Math.min(words.length, startWords.length); j++) {
            if (!compareStrings(words[j], startWords[j])) {
                passed = false
                syntaxErrorIndex = Math.max(syntaxErrorIndex, countIndex(words, j, ""))
                break
            }
        }
        if (passed === true) {
            if (words.length <= startWords.length) {
                syntaxErrorIndex = line.length
                break
            }
            command = arrayOfCommands[i]
            break
        }
    }
    if (command === null) {
        syntaxError(numOfLine + 1, syntaxErrorIndex + 1)
    }
    let res = null
    switch (command) {
        case CommandsEnum.CREATE_CONTACT: {
            res = createContact(line)
            break
        }
        case CommandsEnum.DELETE_CONTACT: {
            res = deleteContact(line)
            break
        }
        case CommandsEnum.DELETE_CONTACTS: {
            res = deleteContacts(line)
            break
        }
        case CommandsEnum.ADD_INFO: {
            res = addInfo(line)
            break
        }
        case CommandsEnum.DELETE_INFO: {
            res = deleteInfo(line)
            break
        }
        case CommandsEnum.SHOW_INFO: {
            res = showInfo(line)
            break
        }
    }
    if (typeof res === "number") {
        syntaxError(numOfLine + 1, res + 1)
    } else if (res !== true) {
        return res
    }
    return true
}

/**
 * Телефонная книга
 */
const phoneBook = new Map();

/**
 * Вызывайте эту функцию, если есть синтаксическая ошибка в запросе
 * @param {number} lineNumber – номер строки с ошибкой
 * @param {number} charNumber – номер символа, с которого запрос стал ошибочным
 */
function syntaxError(lineNumber, charNumber) {
    throw new Error(`SyntaxError: Unexpected token at ${lineNumber}:${charNumber}`);
}

/**
 * Выполнение запроса на языке pbQL
 * @param {string} query
 * @returns {string[]} - строки с результатами запроса
 */
function run(query) {
    let response = []
    let lines = query.split(";")
    if (lines.length === 1 && lines[0] === "") {
        syntaxError(1, 1)
    }
    let checkLast = false
    let n = lines.length
    if (lines[lines.length - 1] !== "") {
        checkLast = true;
    } else {
        n--;
    }
    for (let i = 0; i < n; i++) {
        let res = parse(lines[i], i)
        if (i === lines.length - 1 && checkLast) {
            syntaxError(i + 1, lines[i].length + 1)
        }
        if (res !== true) {
            response = response.concat(res)
        }
    }
    return response;
}

//module.exports = { phoneBook, run };

console.log(run('Покажи  для контактов;'))