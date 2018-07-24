require.config({
	baseUrl : 'resources',
	paths : {
		'app' : 'js/index',
		'angular' : 'lib/angular/angular.min',
		'ngAnimate': 'lib/angular/angular-animate.min',
		'angularAMD' : 'lib/angular/angularAMD',
		'toaster' : 'lib/angular/angular-toaster.min',
		'common' : 'js/common',
		'service' : 'js/index/services',
		'directive':'js/index/directives',
		'controller': 'js/index/controllers',
		'ui.router' : 'lib/angular/angular-ui-router.min',
		'ui.bootstrap' : 'lib/angular/ui-bootstrap-tpls.min',
		'ngTable' : 'lib/angular/ng-table.min',
		'ngResource' : 'lib/angular/angular-resource.min',
		'ngTouch':'lib/angular/angular-touch.min'
	},
	shim : {
		'angular' : {
			'exports' : 'angular'
		},
		'ngAnimate' : ['angular'],
		'ngResource' : ['angular'],
		'toaster' : ['angular', 'ngAnimate'],
		'ui.router' : ['angular'],
		'ui.bootstrap' : [ 'angular' ],
		'ngTouch':['angular'],
		'ngTable' : {
			'exports' : 'ngTable',
			'deps' : [ 'angular' ]
		},
		'angularAMD' : {
			'exports' : 'angularAMD',
			'deps' : [ 'angular' ]
		}
	}
});
require([ 'app/app' ], function(app) {
	app.init();
});