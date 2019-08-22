var Enumerable = function () {
    this.first = function () {
        return this[0]
    }
    this.last = function () {
    	return this[this.length - 1]
    }
}

var list = ["foo", "bar", "baz"]

Enumerable.call(list);

console.log(list.first());
