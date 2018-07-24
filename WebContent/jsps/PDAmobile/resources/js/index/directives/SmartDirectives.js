define(['angular'], function(angular) {
  'use strict';
   angular.module('SmartDirectives',[]).directive("focusMe", function() {
	     return {
	        restrict: "A",
	        link: function(scope, element, attrs) {
	        	element[0].focus();	            
	        }
	    };
    }).directive("enterAsTab",function(){
	   return {
	   	   restrict:"A",
	   	   link:function (scope, element, attrs) {
	        element.bind("keyup", function (event) {
		         if(event.which === 13) {
			    	 var focusable = document.getElementsByTagName('input');
		                // Get the index of the currently focused element
		             var currentIndex = Array.prototype.indexOf.call(focusable, event.target);
		                // Find the next items in the list
		             var nextIndex = currentIndex == focusable.length - 1 ? 0 : currentIndex + 1;
		                // Focus the next element
		             if(nextIndex >= 0 && nextIndex < focusable.length){
		                if(focusable[nextIndex].value == 0||focusable[nextIndex].value == '')
		                       focusable[nextIndex].focus();
		               }	      
			   	   }
	         });
	   	   }
		  }}).directive('stopEvent', function () {
			    return {
			      restrict: 'A',
			      link: function (scope, element, attr) {
			        element.on(attr.stopEvent, function (e) {
			          e.stopPropagation();
			        });
			      }
			    };
		 }).directive('resetField', ['$compile', '$timeout', function($compile, $timeout) {
			    return {
			        require: 'ngModel',
			        scope: {},
			        link: function(scope, el, attrs, ctrl) {					
			            // limit to input element of specific types
			            var inputTypes = /text|search|tel|url|email|password/i;
			            if (el[0].nodeName === "INPUT") {
			                if (!inputTypes.test(attrs.type)) {
			                    throw new Error("Invalid input type for resetField: " + attrs.type);
			                }
			            } else if (el[0].nodeName !== "TEXTAREA") {
			                throw new Error("resetField is limited to input and textarea elements");
			            }
			
			            // compiled reset icon template
			            var template = $compile('<i  ng-show="enabled" ng-click="reset();" class="fa fa-times-circle"></i>')(scope);
			            el.addClass("reset-field");
			            el.after(template);
			
			            scope.reset = function() {
			                ctrl.$setViewValue(null);
			                ctrl.$render();
			                $timeout(function() {
			                    el[0].focus();
			                }, 0, false);
			                scope.enabled = false;
			            };
			
			            el.bind('input', function() {
			                scope.enabled = !ctrl.$isEmpty(el.val());
			            })
			            .bind('focus', function() {
			                $timeout(function() {
			                    scope.enabled = !ctrl.$isEmpty(el.val());
			                    scope.$apply();
			                }, 0, false);
			            })
			            .bind('blur', function() {
			                $timeout(function() {
			                    scope.enabled = false;
			                    scope.$apply();
			                }, 0, false);
			            });
			        }
			    };
          }]).directive('ngsButterbar', ['$rootScope', function($rootScope) {
				return {
					restrict:"A",
					link : function(scope, element) { // attrs
						$rootScope.$on('$routeChangeStart', function() {
							element.show();
							$('div[ui-view]').css('opacity','0.5');
					    });
						$rootScope.$on('$routeChangeSuccess',function() {
							element.hide();				
							$('div[ui-view]').css('opacity','1');
						});
					}
				};
			}]);
});

