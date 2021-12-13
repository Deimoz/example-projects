const ParseError = function (message, name) {
    const err = function (...args) {
        this.message = message(...args);
    };
    err.prototype = new Error;
    err.prototype.name = name;
    return err;
};

const MissingArgumentError = ParseError(
    (index) => "Missing argument on position " + index,
    "MissingArgumentError"
);

const TooMuchArgumentsError = ParseError(
    (symbol) => "Too much arguments beginning from " + symbol,
    "TooMuchArgumentsError"
);

const MissingOperationError = ParseError(
    (index) => "Missing operation on position " + index,
    "MissingOperationError"
);

const MissingCloseBracketError = ParseError(
    (index) => "Missing close bracket on position " + index,
    "MissingCloseBracketError"
);

const IllegalSymbolError = ParseError(
    (symbol) => "Illegal symbol " + symbol,
    "IllegalSymbolError"
);

const Const = function (value) {
    this.value = value;
};

Const.prototype.evaluate = function (...values) {
    return this.value;
};

Const.prototype.toString = function () {
    return this.value.toString();
};

Const.prototype.prefix = Const.prototype.toString;

const Variable = function (name) {
    this.name = name;
};

Variable.prototype.evaluate = function (...values) {
    switch (this.name) {
        case "x": return values[0];
        case "y": return values[1];
        case "z": return values[2];
    };
};

Variable.prototype.toString = function () {
    return this.name;
};

Variable.prototype.prefix = Variable.prototype.toString;

function AbstractOp(...args) {
    this.operands = args;
}

AbstractOp.prototype.toString = function () {
    return this.operands.join(" ") + " " + this.symbol;
}

AbstractOp.prototype.prefix = function() {
    return "(" + this.symbol + " " + this.operands.map((operand) => operand.prefix()).join(" ") + ")";
}

AbstractOp.prototype.evaluate = function (...values) {
    let res = [];
    for (let i = 0; i < this.operands.length; i++) {
        res.push(this.operands[i].evaluate(...values));
    }
    return this.opFunc(...res);
}

function opConstuctor(func, symbol) {
    let res = function(...args) {
        return AbstractOp.apply(this, args);
    }
    res.prototype = new AbstractOp;
    res.prototype.opFunc = func;
    res.prototype.symbol = symbol;
    return res;
}

const Add = opConstuctor((a, b) => a + b, "+");

const Subtract = opConstuctor((a, b) => a - b, "-");

const Multiply = opConstuctor((a, b) => a * b, "*");

const Divide = opConstuctor((a, b) => a / b, "/");

const Negate = opConstuctor((a) => -a, "negate");

const Sinh = opConstuctor((a) => Math.sinh(a), "sinh");

const Cosh = opConstuctor((a) => Math.cosh(a), "cosh");

const variables = {
    "x" : 0,
    "y" : 1,
    "z" : 2,
}

const operations = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "negate": Negate,
    "sinh" : Sinh,
    "cosh" : Cosh,
}

const numOfargs = {
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate": 1,
    "sinh": 1,
    "cosh": 1,
}

const isNumber = function (number) {
    let i = 0;
    if (number[i] === "-") {
        i++;
    }
    if (i === number.length) {
        return false;
    }
    while (i < number.length) {
        if (number[i] < "0" || number[i] > "9") {
            return false;
        }
        i++;
    }
    return true;
};

const Source = function(expression) {
    this.expression = expression;
    this.currentElement;
    this.currentIndex = 0;
}

Source.prototype.nextElement = function() {
    this.skipWhitespace();
    if (this.expression[this.currentIndex] === "(" || this.expression[this.currentIndex] === ")") {
        this.currentElement = this.expression[this.currentIndex];
        this.currentIndex++;
    } else {
        let endIndex = this.currentIndex;
        while (endIndex < this.expression.length && this.expression[endIndex] !== "(" && this.expression[endIndex] !== ")" && this.expression[endIndex] !== " ") {
            endIndex++;
        }
        this.currentElement = this.expression.slice(this.currentIndex, endIndex);
        this.currentIndex = endIndex;
    }
}

Source.prototype.skipWhitespace = function () {
    while (this.currentIndex < this.expression.length && this.expression[this.currentIndex] === " ") {
        this.currentIndex++;
    }
};

Source.prototype.isEnd = function() {
    this.skipWhitespace();
    return this.currentIndex === this.expression.length;
}

Source.prototype.parseExpression = function() {
    this.skipWhitespace();
    let res = this.parse();

    if (!this.isEnd()) {
        throw new IllegalSymbolError(this.currentElement);
    }

    return res;
}

Source.prototype.getExpression = function() {
    if (this.currentElement === "(") {
        let startIndex = this.currentIndex - 1;
        this.nextElement();
        while (this.currentIndex < this.expression.length && this.currentElement !== ")") {
            if (this.currentElement === "(") {
                this.getExpression();
            }
            this.nextElement();
        }
        if (this.currentElement !== ")") {
            throw new MissingCloseBracketError(this.currentIndex);
        }
        let endIndex = this.currentIndex;
        return this.expression.slice(startIndex, endIndex);
    } else {
        return this.currentElement;
    }
}

Source.prototype.parse = function() {
    this.nextElement();
    if (this.currentElement === "(") {
        this.nextElement();
        if (this.currentElement in operations) {
            let oper = this.currentElement;
            let elementsStack = [];
            this.nextElement();
            for (let i = 0; i < this.expression.length && this.currentElement !== ")"; i++) {
                elementsStack.push(parsePrefix(this.getExpression()));
                this.nextElement();
            }

            if (this.currentElement !== ")") {
                throw new MissingCloseBracketError(this.currentIndex);
            }

            if (elementsStack.length > numOfargs[oper]) {
                throw new TooMuchArgumentsError(this.currentElement);
            } else if (elementsStack.length < numOfargs[oper]) {
                throw new MissingArgumentError(this.currentIndex);
            }

            return new operations[oper](...elementsStack);
        } else {
            throw new MissingOperationError(this.currentIndex);
        }
    } else if (this.currentElement in variables) {
        return new Variable(this.currentElement);
    } else if (isNumber(this.currentElement)) {
        return new Const(parseInt(this.currentElement));
    } else {
        throw new IllegalSymbolError(this.currentElement);
    }
}

const parsePrefix = function(expression) {
    let source = new Source(expression);
    return source.parseExpression();
}