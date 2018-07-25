Ext.QuickTips.init();
Ext.define('erp.controller.common.LabelButton', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.labelButton.Viewport','common.labelButton.Form','core.trigger.DbfindTrigger','core.form.FtField',
     		'core.form.FtFindField','core.form.ConDateField','core.form.MonthDateField'
     	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpLabelButtonFormPanel button[name=confirm]': {
    			click: function(btn){
    				var form = Ext.getCmp('labelbuttonform');
//    				var param = '';
    				var params = {};
    				params.caller = caller;
    				var data = {};
    				Ext.each(form.items.items,function(item,index){
    					if(item.logic != null && item.logic!=""){
    						data[item.logic] = item.rawValue;
    					}
    				});
    				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
    				console.log(params);
    	    		Ext.Ajax.request({
    	    			//confirmUrl为在对应view js中创建此form时赋值的地址
    	    			url:basePath+form.dealUrl,
    	    			params:params,
    	    			method:'post',
    	    			callback:function(options,success,response){
    	    				var localJson = new Ext.decode(response.responseText);
    	    				if(localJson.success){
    	    					//执行成功
    	    					Ext.Msg.alert('操作成功!');
    	    					window.location.reload();
    	    				}else{
    	    					//执行失败
    	    					Ext.Msg.alert('操作失败!');
    	    					window.location.reload();
    	    				}
    	    			}
    	    		});
    			}
    		},
    		'erpLabelButtonFormPanel': {
    			titlechange: function(f){
    				if(f.title != null){
    					f.ownerCt.setTitle(f.title);
    					f.dockedItems.items[0].hide();
    				}
    			}
    		},
    		'monthdatefield': {
    			afterrender: function(f) {
    				me.getCurrentYearmonth(f);
    			}
    		}
    	});
    },
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'fa/ars/getCurrentYearmonth.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				}
			}
		});
	}
});