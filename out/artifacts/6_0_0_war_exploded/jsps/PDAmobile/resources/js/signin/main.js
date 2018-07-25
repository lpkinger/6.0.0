require.config({
	baseUrl : 'resources',
	paths : {
		'app' : 'js/signin',
		'angular' : 'lib/angular/angular.min',
		'ngAnimate': 'lib/angular/angular-animate.min',
		'toaster' : 'lib/angular/angular-toaster.min',
		'services' : 'js/common/services'
	},
	shim : {
		'angular' : {
			'exports' : 'angular'
		},
		'ngAnimate' : ['angular'],
		'toaster' : ['angular', 'ngAnimate'],
		'services': ['angular']
	}
});
require([ 'app/app' ], function(app) {
	app.init();
});