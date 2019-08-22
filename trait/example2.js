var Resolvable = (function () {
	var isString = function (type) {
		return (typeof type === 'string')
	}
	var isFunction = function (type) {
		return (typeof type === 'function')
	}
	var resolveBefore = function (methodName, rivalingMethod) {
		if (isString(methodName) && isFunction(rivalingMethod)) {
			var type = this
			if (isFunction(type[methodName])) {
				type[methodName] = type[methodName].before(rivalingMethod, type)
			} else {
				type[methodName] = rivalingMethod
			}
		}
	}
	var resolveAfter = function (methodName, rivalingMethod) {
		if (isString(methodName) && isFunction(rivalingMethod)) {
			var type = this
			if (isFunction(type[methodName])) {
				type[methodName] = type[methodName].after(rivalingMethod, type)
			} else {
				type[methodName] = rivalingMethod
			}
		}
	}
	var resolveAround = function (methodName, rivalingMethod) {
		if (isString(methodName) && isFunction(rivalingMethod)) {
			var type = this
			if (isFunction(type[methodName])) {
				type[methodName] = type[methodName].around(rivalingMethod, type)
			} else {
				type[methodName] = rivalingMethod
			}
		}
	}
	var resolveWithAlias = function (methodName, aliasName, rivalingMethod) {
		if (isString(methodName) && isString(aliasName) && (methodName != aliasName) && isFunction(rivalingMethod)) {
			this[aliasName] = rivalingMethod
		}
	}
	var Mixin = function () {
		var type = this
		type.resolveBefore    = resolveBefore
		type.resolveAfter     = resolveAfter
		type.resolveAround    = resolveAround
		type.resolveWithAlias = resolveWithAlias
	}
	return Mixin
}())
