"use strict"

let x, y, z;

const cnst = value => () => value;

const variable = (name) => (...values) => {
    switch (name) {
        case "x": return values[0];
        case "y": return values[1];
        case "z": return values[2];
    };
};

const abstractOp = (func) => {
    return function(...args) {
        return function (...values) {
            let res = [];
            for (let i = 0; i < args.length; i++) {
                res.push(args[i](...values));
            }
            return func(...res);
        }
    }
}

const add = abstractOp((a, b) => a + b);

const subtract = abstractOp((a, b) => a - b);

const multiply = abstractOp((a, b) => a * b);

const divide = abstractOp((a, b) => a / b);

const negate = abstractOp((a) => -a);

const avg5 = abstractOp((a, b, c, d , e) => (a + b + c + d + e) / 5)

const med3 = abstractOp((a, b, c) => Math.min(Math.max(a, b), Math.min(Math.max(a, c), Math.max(b, c))));

const pi = cnst(Math.PI);

const e = cnst(Math.E);

const variables = {
    "x" : variable("x"),
    "y" : variable("y"),
    "z" : variable("z"),
}

const mathConst = {
    "pi" : pi,
    "e"  : e,
}

const operations = {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "negate": negate,
    "avg5": avg5,
    "med3": med3,
}

const numOfargs = {
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate": 1,
    "avg5": 5,
    "med3": 3,
}

const parse = function(expression) {
    let elements = expression.split(" ").filter(elem => elem.length > 0);
    let elementsStack = [];
    elements.forEach(element => {
        if (element in variables) {
            elementsStack.push(variables[element]);
        } else if (element in mathConst) {
            elementsStack.push(mathConst[element]);
        } else if (element in operations) {
            let argsOfOp = [];
            for (let j = 0; j < numOfargs[element]; j++) {
                argsOfOp.push(elementsStack.pop());
            }
            argsOfOp.reverse();
            elementsStack.push(operations[element](...argsOfOp));
        } else {
            elementsStack.push(cnst(parseInt(element)));
        }
    });
    return elementsStack.pop();
}