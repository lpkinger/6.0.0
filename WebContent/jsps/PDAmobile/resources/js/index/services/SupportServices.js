define([ 'angular'], function() {
	angular.module('SupportServices', [ ]).factory('SupportUtil', function() {
		return {
			contains : function(array, element,field) {//根据ID判断数组是否包含某个元素
				var result = false;
				array = eval('(' + array + ')');
				angular.forEach(array, function(value, key){
					if(value[field] == element) {//ID相等即包含
						result = true; 
						return result;
					}
				});
				return result;
			  }	,
	   	   lapFor: function(number,length){
			while (number.length < length) { 
					number = "0" + number;
			 }	
			number = number.substring(0, length);
			return number;
		  },
		  lapAft: function(number,length){
			while (number.length < length) { 
					number = "0" + number;
			 }	
			number = number.substring(number.length-length, number.length);
			return number;
		 }	   	  
	   	  
		}
	}).factory('smartyConfig',['$http','$filter','BaseService', function($http,$filter,BaseService) {
	   return {
	      getSmartySuggestions :function(prefix,type) {
			   var requestUrl = BaseService.getRootPath()+'/pda/fuzzySearch.action',
		       requestParams = {};
		       requestParams["inoutNo"] = escape(prefix.toLowerCase());
		       requestParams["type"] = escape(type.toLowerCase());
		       return $http.post(requestUrl,requestParams,{cache: true}).then(function(response){		   
					 return response.data;			 
		        });
	      }			 
	   }		
	}]).factory("smartySuggestor", ["smartyConfig", function(smartyConfig) {
		return{
			getSmartySuggestions :smartyConfig.getSmartySuggestions
		}
	}]);
});