/**
 * 在途在库拆分解锁
 */	
Ext.define('erp.view.core.button.DeblockSplit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeblockSplitButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDeblockSplitButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
        handler:function(btn){
        	var me = this ;
            win=me.DeblockSplit(btn);
            win.show();
        },
        DeblockSplit:function(btn){
        	return Ext.create('Ext.window.Window',{
				   width : 530,
				   height : 250,
	        	   title: '<h1>在途在库拆分</h1>',
     		   layout: {
     			   type: 'fit'
     		   },
     		   items:[{
     			   xtype : 'form',
     			   frame : true ,
	   			   layout : {
	   				   type : 'column'
	   			   },
     			   items:[{
     				   xtype : 'dbfindtrigger',
     				   id : 'ob_sddetno' ,
     				   name : 'ob_sddetno',
     				   fieldLabel : '订单行号',
     				   margin:'20 0 0 0', 
     				   fieldStyle:'background:#fffac0;color:#515151;'
     			   },{
     				   xtype : 'textfield',
     				   id : 'ob_sacode' ,
     				   name : 'ob_sacode',
     				   fieldLabel : '订单号',
     				   margin:'20 0 0 0', 
     				   readOnly:true,
     				   fieldStyle:'background:#fffac0;color:#515151;'
     			   },{
     				   xtype : 'hidden',
     				   id : 'ob_saleid' ,
     				   name : 'ob_saleid',
     				   fieldLabel : '订单ID',
     				   margin:'20 0 0 0', 
     				   fieldStyle:'background:#fffac0;color:#515151;'
     			   },{
     				   xtype : 'dbfindtrigger',
    				   id : 'ob_sfdetno' ,
    				   name : 'ob_sfdetno',
    				   fieldLabel : '预测单行号',
    				   margin:'20 0 0 0', 
    				   fieldStyle:'background:#fffac0;color:#515151;'
     			   },{
     				   xtype : 'textfield',
     				   id : 'ob_sfcode' ,
     				   name : 'ob_sfcode',
     				   fieldLabel : '预测单号',
     				   margin:'20 0 0 0', 
     				   readOnly:true,
     				   fieldStyle:'background:#fffac0;color:#515151;'
     			   },{
     				   xtype : 'hidden',
     				   id : 'ob_forecastid' ,
     				   name : 'ob_forecastid',
     				   margin:'20 0 0 0', 
     				   fieldLabel : '预测单ID',
     				   fieldStyle:'background:#fffac0;color:#515151;'
     			   },{
    				   xtype : 'datefield',
    				   id : 'ob_delivery' ,
    				   name : 'ob_delivery',
    				   margin:'20 0 0 0', 
    				   fieldLabel : '需求日期',
    				   fieldStyle:'background:#fffac0;color:#515151;'
    			   }]    		        			   
     		   }],buttonAlign: 'center',
	        		buttons: [{
	        			xtype: 'button',
	        			text: '确 定',
	        			width: 60,
	        			iconCls: 'x-button-icon-save',
	        			handler: function(btn) {
	        				var dd = new Object();
	        				var ob_sddetno=Ext.getCmp('ob_sddetno');
	        				var ob_sacode=Ext.getCmp('ob_sacode');
	        				var ob_sfdetno=Ext.getCmp('ob_sfdetno');
	        				var ob_sfcode=Ext.getCmp('ob_sfcode');
	        				var ob_saleid=Ext.getCmp('ob_saleid');
	        				var ob_forecastid=Ext.getCmp('ob_forecastid');
	        				var ob_delivery=Ext.getCmp('ob_delivery');
	        				dd['ob_sddetno'] = ob_sddetno.value;
	        				dd['ob_sacode'] = ob_sacode.value;
	        				dd['ob_sfdetno'] = ob_sfdetno.value;
	        				dd['ob_saleid'] = ob_saleid.value;
	        				dd['ob_sfcode'] = ob_sfcode.value;
	        				dd['ob_forecastid'] = ob_forecastid.value;
	        				dd['ob_delivery'] = ob_delivery.value;
	        				if(ob_sacode.value&&ob_sfcode.value){
	        					showError("只能选一个");
	        					return;
	        				}
	        				var grid = Ext.getCmp("batchDealGridPanel"); 
	        				var record = grid.selModel.selected.items;
	        				var jsonGridData = new Array();
	        				for(var i=0;i<record.length;i++){
	        					var data = record[i].data;
        					    var r=new Object();
            				    r['ob_id']=data.ob_id;
            				    r['type']=data.type;
            				    r['ob_tqty']=data.ob_tqty; 
            				    jsonGridData.push(Ext.JSON.encode(r));
	        				}
	        				var params=new Object();
	        				params.formdata = unescape(Ext.JSON.encode(dd).replace(/\\/g,"%"));
	        				params.data = unescape(jsonGridData.toString().replace(/\\/g,"%"));
	    					Ext.Ajax.request({
	    				   		url : basePath + 'scm/splitDeblock.action',
	    				   		params: {
	    				   			formdata: params.formdata,
	    				   			data:params.data
	    				   		},
	    				   		method : 'post',
	    				   		callback : function(options,success,response){
	    				   			var localJson = new Ext.decode(response.responseText);
	    			    			if(success){
	    			    				window.location.reload();
	    			    				showMessage('拆分','拆分成功!');
	    				   			} else if(localJson.exceptionInfo){
	    				   				var str = localJson.exceptionInfo;
	    				   				showError(str);
	    				   				} 
	    			        		}
	    					});
	        			}
	        		},
	        		{
	        			xtype: 'button',
	        			columnWidth: 0.1,
	        			text: '关闭',
	        			width: 60,
	        			iconCls: 'x-button-icon-close',
	        			margin: '0 0 0 10',
	        			handler: function(btn) {
	        				var win = btn.up('window');
	        				win.close();
	        				win.destroy();
	        			}
	        		}],
			   })	   
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});