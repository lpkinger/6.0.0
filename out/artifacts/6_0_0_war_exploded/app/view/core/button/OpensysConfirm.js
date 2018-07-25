/**
 * 客户服务 确认
 */
Ext.define('erp.view.core.button.OpensysConfirm',{ 
		extend: 'Ext.Button', 
		FormUtil: Ext.create('erp.util.FormUtil'),
		alias: 'widget.erpOpensysConfirmButton',
		param: [],
		id: 'erpOpensysConfirmButton',
		text: $I18N.common.button.erpOpensysConfirmButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender:function(btn){
				var form =btn.ownerCt.ownerCt;
				var confirm = form.down('field[name=confirmfield]');
				var status = Ext.getCmp(form.statuscodeField);
    				if((status && status.value != 'AUDITED')||!confirm||(confirm&&confirm.value==-1)){
    					btn.hide();
    				}
			},
			click:function(btn){
				var me=this;
				var form =btn.ownerCt.ownerCt;
	    	    var win =Ext.create('Ext.Window', {
				  		   title:'确认',
				    	   items:[Ext.widget('form',{	    			 
				    			bodyStyle : 'background:#f9f9f9;padding:15px 15px 0',
				    			fieldDefaults : {
				    				   msgTarget: 'none',
				    				   blankText : $I18N.common.form.blankText,
				    				   cls:'single-field'
				    		    },
				    			items:[{
				    				   fieldLabel:'结果',
				    				   name:'result',
				    				   id:'result',
				    				   xtype:'combo',
				    				   displayField: "display",
				    				   valueField: "value",
				    				   store: {data: [{display: "同意", value: "同意"}, {display: "不同意", value: "不同意"}],
				    				   fields: ["display", "value"]},
				    				   style:'padding-left:5px',
				    				   value: "同意",
				    				   checked:true
				    			   },{
				    				   xtype:'textarea',
				    				   fieldLabel:'备注信息',
				    				   width:480,
				    				   maxLength:300,
				    				   maxLengthText:'输入太长了!',
				    				   name:'desc',
				    				   id:'desc',
				    				   emptyText:'请填写确认信息',
				    				   fieldStyle:'background:white repeat-x 0 0;border-width: 1px;border-style: solid;'
				    			   }]								
				    		   })],
				    		   buttonAlign:'center',
				    		   buttons:[{
				    			   text:'确认',
				    			   handler:function(btn){
				    			   		var keyField=form.keyField;
				    			   		var res=Ext.getCmp('result').value;
				    			   		var desc=Ext.getCmp('desc').value;
				    			   		if(keyField&&Ext.getCmp(keyField).value&&caller){
				    			   			var keyvalue=Ext.getCmp(keyField).value;
				    			   			form.setLoading(true);
				    			   			Ext.Ajax.request({
					    					   url : basePath  + 'opensys/openSysConfirmCommon.action?caller='+caller,
					    					   params: {
					    						   id:keyvalue,
					    						   confirmres:res,
					    						   confirmdesc:desc
					    					   },
					    					   method : 'post',
					    					   callback : function(options,success,response){	
					    						   form.setLoading(false);
					    						   var localJson = new Ext.decode(response.responseText);
					    						   if(localJson.exceptionInfo){
					    							   var str = localJson.exceptionInfo;
					    							   showError(str);
					    						   }else{
					    							   showMessage('提示','确认成功!',1000);
					    							   window.location.reload();
					    							  
					    						   }
					    					   }
					    					});
				    			   		}
				    			   }
				    		   },{
				    			   text:'取消',
				    			   handler:function(btn){
				    				   btn.ownerCt.ownerCt.close();
				    			   }
				    		   }]
				    	   });
				    	   win.show();
			}
		}
	});