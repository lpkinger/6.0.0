Ext.QuickTips.init();
Ext.define('erp.controller.common.ProductRelative', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.productRelative.ProductRelative','common.productRelative.Form','core.trigger.MultiDbfindTrigger',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField'    	  
     	],
    init:function(){  	
    	var me = this;
    	var cal;
		if(caller=='ProductRelative!Query'){
			cal = 'Product';
		}
		if(caller=='ProjectRelative!Query'){
			cal = 'Project';
		}
		if(caller=='GroupProductRelative!Query'){
			cal = 'GroupProduct';
		}
    	this.control({
    	  	'field[name=pr_code]': {
    	  		afterrender: function(f){
    	  			var val = getUrlParam('pr_code');
    	  			if(!Ext.isEmpty(val)){
	    	  			f.setValue(getUrlParam('pr_code'));
	    	  			if(f.xtype == 'dbfindtrigger') {
							f.autoDbfind('form', caller, f.name, f.name + " like '%" + val + "%'");
						}
    	         	}
    	  		},
				change: function(f,newvalue,oldvalue){
					if(newvalue != null && newvalue!= ''){
						var html='<iframe src="' + basePath + 'jsps/common/relativeSearch.jsp?whoami=' + cal + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>';
						Ext.getCmp('ProductWh').update(html);
					}
				}
    	  	},
    	  	'field[name=whichsystem]' : {
    	  		change: function(f){
    	  			//var code = Ext.getCmp('pr_code');
					/*if(code.value != null && code.value != ''){*/
					var html='<iframe src="' + basePath + 'jsps/common/relativeSearch.jsp?whoami=' + cal + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					Ext.getCmp('ProductWh').update(html);
					/*}*/
				}
    	  	},
    	  	'field[name=prj_code]': {
    	  		afterrender: function(f){
    	  			var val = getUrlParam('prj_code');
    	  			if(!Ext.isEmpty(val)){
	    	  			f.setValue(getUrlParam('prj_code'));
	    	  			if(f.xtype == 'dbfindtrigger') {
							f.autoDbfind('form', cal, f.name, f.name + " like '%" + val + "%'");
						}
    	         	}
    	  		},
				change: function(f){
					if(f.value != null && f.value != ''){
						var html='<iframe src="' + basePath + 'jsps/common/relativeSearch.jsp?whoami=' + cal + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						Ext.getCmp('ProductWh').update(html);
					}
				}
    	  	}
    	});
    }
});