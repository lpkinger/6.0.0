Ext.QuickTips.init();
Ext.define('erp.controller.oa.myProcess.synergy.NewSynergy', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.myProcess.synergy.NewSynergy','core.form.Panel','core.button.Save','core.button.Close',
    		'core.button.Over','core.button.Submit','core.form.FileField','core.form.HrOrgSelectField',
    		'core.button.Update','core.button.Delete','core.form.ConDateHourMinuteField',
    		'core.form.YnField','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var mm = this.FormUtil;
    				mm.beforeSave(this);
    				var form = Ext.getCmp('form');
    				if(! mm.checkForm()){
    					return;
    				}
//    				if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
//    					mm.getSeqId(form);
//    				}
    				if(form.getForm().isValid()){
    					me.saveCustomFlow(form.getForm().getValues()['sy_id']);
    				}else{
    					mm.checkForm();
    				}
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('sy_id').value);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'erpOverButton': {
    			click: function(btn){
    				Ext.getCmp('sy_status').setValue('已结束');
    				Ext.getCmp('sy_statuscode').setValue('OVERED');
    				this.FormUtil.onUpdate(this);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('sy_id').value);
    			}
    		},
    		'button[id=addprocess]':{
    			click: function(btn){
    				var panel = Ext.getCmp("process"); 
    	    		var main = parent.Ext.getCmp("content-panel");
    	    		if(!panel){ 
    	    			var title = "添加流程";
    	    			panel = { 
    	    					title : title,
    	    					tag : 'iframe',
    	    					tabConfig:{tooltip: '添加流程'},
    	    					frame : true,
    	    					border : false,
    	    					layout : 'fit',
    	    					iconCls : 'x-tree-icon-tab-tab1',
    	    					html : '<iframe id="iframe_' + 1 + '" src="' + basePath + 'jsps/oa/myProcess/customFlow.jsp" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
    	    					closable : true,
    	    					listeners : {
    	    						close : function(){
    	    							main.setActiveTab(main.getActiveTab().id); 
    	    						}
    	    					} 
    	    			};
    	    			var win = Ext.create('Ext.window.Window', {
        				    title: '自定义流程',
        				    height: 300,
        				    width: 640,
        				    layout: 'fit',
        				    items: [panel]
        				});        				
        				win.show();
    	    		}else{ 
    	    			main.setActiveTab(panel); 
    	    		}
    			}
    		},
    		'htmleditor[id=sy_content]': {
    			afterrender: function(f){
    				f.setHeight(325);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	saveCustomFlow: function(){
		var syId = arguments[0];
		var customFlowDetail = iframe_1.window.Ext.getCmp('grid');
	    var cparam = []; 
	    if(!customFlowDetail){
	    	alert('你尚未添加流程!');
	    }else{
	    	var datas = customFlowDetail.store.data.items;
	    	Ext.each(datas,function(data){
	    		if(data.data['cfd_actorUsers']!=null&&data.data['cfd_actorUsers']!=''){
	    			cparam.push(data.data);
	    		 }
	    	});
	    }	        
	    var cArray = [];
	    Ext.each(cparam,function(param){
	        var ps = Ext.encode(param);
	        cArray.push(ps);
		});
		var param = '['+cArray.join(",")+']';
		var customFlow = iframe_1.window.Ext.getCmp('form');
		var cf_sourceId = iframe_1.window.Ext.getCmp('cf_sourceId');
		cf_sourceId.setValue(syId);
			
		var cf_id =  iframe_1.window.Ext.getCmp('cf_id');
		cf_id.setValue(syId);
			
		var cf_caller = iframe_1.window.Ext.getCmp('cf_caller');
		cf_caller.setValue("CustomFlow_"+new String(syId));
			
		var cf_name = iframe_1.window.Ext.getCmp('cf_name');
		cf_name.setValue("CustomFlow_"+new String(syId));
			
		var cf_source = iframe_1.window.Ext.getCmp('cf_source');
		cf_source.setValue("CustomFlow");
			
		console.log("ssddddddd");
		var formStore = Ext.encode(customFlow.getForm().getValues());
		Ext.Ajax.request({
			url: basePath + customFlow.saveUrl,
			params: {
			    formStore:formStore,
			    param:param
			},
			success: function(response){
			    alert("成功");
			}
		});   	
   }
});