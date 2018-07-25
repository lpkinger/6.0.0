Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMTree', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.BOMTree','common.query.Form','pm.bom.BOMTreeGrid','core.form.YnField',
    		'core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    		'bomTreeGrid': {
    			itemmousedown: function(selModel, record){  
    				if(record.data['bs_sonbomid'] > 0 && record.childNodes.length == 0 ){
    					Ext.getCmp('querygrid').loadChildNodes(record);
    				}
    			}
    		},
    		'button[name=export]': {
    			afterrender: function(btn){ 
    				 btn.hide();
    			}
    		},
    		'button[name=refresh]':{
    			afterrender: function(btn){ 
    				 btn.hide();
    			}
    		},
    		'button[id=query]':{
    			afterrender: function(btn){ 
   				 	btn.handler = function(){
					 	var form=btn.ownerCt.ownerCt;
					 	var bomid = Ext.getCmp('bo_id').value;
	    				if(bomid==null || bomid == ''){
	    					showError('请先选择BOMID!');
	    					return;
	    				}
			    		if(form.prevTime==null){
			    			form.prevTime=new Date().getTime();
			    			form.onQuery();
			    		}else {
			    			var nowtime=new Date().getTime();
			    			if((nowtime-form.prevTime)/1000<2){
			    				showError('请控制筛选时间间隔不能小于2秒!');
			    				return;
			    			}else {
			    				form.prevTime=nowtime;
			    				form.onQuery();
			    			}
			    		}
   				 	};
    			}
    		}
    	});
    }
});