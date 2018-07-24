Ext.QuickTips.init();
Ext.define('erp.controller.ma.data.Export', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.data.Export','core.form.Panel','core.form.ArgsField','core.form.ArgTypeField',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.Load','core.button.Export',
   		'core.button.Update','core.button.Test','core.button.Scan','core.toolbar.Toolbar','core.button.Delete',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.button.DeleteDetail'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(btn);
    			}
    		},
    		'erpUpdateButton':{
    			click:function(btn){
    				var form=me.getForm(btn);
    				var data=form.getForm().getValues();
    				var condition=data.ed_condition;
    				var tablename=data.ed_tablename;
    				var orderby=data.ed_orderby;
    				var fields=data.ed_fields.replace(/#/g,",");
    				var str="";
    				str+=(condition==""||condition==null)?"":(" WHERE "+condition);
    				str+=(orderby==""||orderby==null)?"":" "+orderby;
    				Ext.getCmp('ed_sql').setValue('SELECT '+fields+' FROM ' +tablename+str);
    				me.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton':{
    			click:function(btn){
    			  me.FormUtil.onDelete(Ext.getCmp('ed_id').getValue());
    			}
    		},
    		'multidbfindtrigger[name=ed_tables]':{
    	       change:function(field,newvalue){
    	    	   if(newvalue!=null){
    	    		   var str="(";
    	    		   Ext.Array.each(newvalue.split('#'),function(item){   			   
        	    			   str+="'"+item+"',";  
    	    		   });
    	    		   str=str.substring(0,str.length-1)+")";
    	    		   Ext.getCmp('ed_selectfields').dbBaseCondition="ddd_tablename in " +str;
    	    	   }
    	    	   var com=Ext.getCmp('ed_selectfields');
    	    	   com.setValue(null);
    	    	   if(newvalue!=""){
    	    	   com.setDisabled(false);
    	    	   }else com.setDisabled(true);
    	    	   Ext.getCmp('ed_tablename').setValue(null);
    	    	   Ext.getCmp('ed_fields').setValue(null);
    	       }
    		},
    	    'multidbfindtrigger[name=ed_selectfields]':{
    			afterrender:function(trigger){
    				trigger.disabled=true;
    			}
    		},    		
    		'erpTestButton':{
    		  click:function(btn){
    			  var form=me.getForm(btn);
    			  Ext.Ajax.request({
    				 method:'POST',
    				 url:basePath+form.testUrl,
    				 params:{
    					 formStore:unescape(Ext.JSON.encode(form.getForm().getValues()).replace(/\\/g,"%"))
    				 },
    			     callback : function(options,success,response){
    				   var local=Ext.decode(response.responseText);
    				   if(local.success){
    					   Ext.Msg.alert('提示','测试成功!');
    				   }else Ext.Msg.alert('提示','测试失败!');
    				 }
    			  });
    		  },
    		  beforerender:function(btn){
    			 btn.formBind=true;
    		  }
    		},
    		'erpLoadButton':{
    			click:function(btn){
    				var form=me.getForm(btn);
    				 Ext.Ajax.request({
        				 method:'POST',
        				 url:basePath+form.loadUrl,
        				 params:{
        					id:Ext.getCmp('ed_id').getValue()
        				 },
        			     callback : function(options,success,response){
        				   var local=Ext.decode(response.responseText);
        				 if(local.exceptionInfo){
        			        	  showError(local.exceptionInfo);
        			        	  return;
        			   }else if(local.success){
        				btn.ownerCt.ownerCt.ownerCt.add({
        					xtype:'grid',
        					columns:local.columns,
        					anchor: '100% 30%',
        				 	emptyText : $I18N.common.grid.emptyText,
        				    columnLines : true,
        				    autoScroll : true,
        					store:Ext.create('Ext.data.Store', {
        					    fields: local.fields,
        					    data: Ext.decode(local.data)})
        				});   
        			    }
        				 }
        			  });
    				
    			},
    		 afterrender:function(btn){
    			  btn.id='load';
    		  }
    		},
    		'erpExportButton':{
    		 beforerender:function(btn){
    			 btn.handler=function(){
    				 var id=Ext.getCmp('ed_id').getValue();
    				 window.location.href=basePath+'ma/downloadAsExcel.action?id='+id;
    			 };
    		 },
    		 afterrender:function(btn){
    			 var id=Ext.getCmp('ed_id').getValue();
    			 if(id!=""&&id!=null){
    				 btn.setDisabled(false);
    				 Ext.getCmp('load').setDisabled(false);
    			 }else{
    				 btn.setDisabled(true);
    				 Ext.getCmp('load').setDisabled(true);
    			 }
    		 }
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form=me.getForm(btn);
		var data=form.getForm().getValues();
		var condition=data.ed_condition;
		var tablename=data.ed_tablename;
		var orderby=data.ed_orderby;
		var fields=data.ed_fields.replace(/#/g,",");
		var str="";
		str+=(condition==""||condition==null)?"":(" WHERE "+condition);
		str+=(orderby==""||orderby==null)?"":" "+orderby;
		Ext.getCmp('ed_sql').setValue('SELECT '+fields+' FROM ' +tablename+str);
		if(Ext.getCmp('ed_code').value == null || Ext.getCmp('ed_code').value == ''){
			me.BaseUtil.getRandomNumber();
			Ext.getCmp('ed_code').setValue('D_'+Ext.getCmp('ed_code').getValue() );
		}
		me.FormUtil.beforeSave(me);
	}
});