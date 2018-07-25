define([ 'lib/i18n!nls/lang' ], function(localeStrings) {
	'use strict';
	angular.module("ng.local", []).factory('locale', function() {
		return localeStrings;
	}).factory('localizationService', [ 'locale', function(locale) {
		function getLocalizedValue(path) {
			var keys = path.split('.');
			return getValue(keys);
		}
		function getValue(keys) {
			var level = 0;
			function get(context) {
				if (context[keys[level]]) {
					var val = context[keys[level]];
					if (typeof val === 'string' || typeof val === 'number') {
						return val;
					} else {
						level++;
						return get(val);
					}
				} else {
					console.error('Missing localized string for: ', keys);
				}
			}
			return get(locale);
		}
		return {
			getLocalizedString : function(path) {
				return getLocalizedValue(path, locale);
			}
		};
	} ]).directive('i18n', [ 'localizationService', function(localizationService) {
		return {
			restrict : "A",
			link : function(scope, element, attrs) {
				var string = localizationService.getLocalizedString(attrs.i18n);
				element.text(string);
			}
		};
	} ]).filter('i18n', [ 'localizationService', function(localizationService) {
		return function(input) {
			return localizationService.getLocalizedString(input);
		};
	} ]);
});