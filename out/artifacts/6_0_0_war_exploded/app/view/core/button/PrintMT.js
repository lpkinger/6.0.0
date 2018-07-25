/**
 * 多次打印
 */	
Ext.define('erp.view.core.button.PrintMT',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintMTButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintMTButton,
    	style: {
    		marginLeft: '10px'
        },
        width:90,
        beforePrint: function(f,callback) {
    		var me = this;
    		Ext.Ajax.request({
    			url: basePath + 'common/report/getFields.action',
    			method: 'post',
    			params:{
    				caller:f
    			},
    			callback: function(opt, s, r) {
    				var rs = Ext.decode(r.responseText);    				
    				callback.call(null,rs);
    			}
    		});
    	},
        handler: function(){
    		var me = this;
    		me.beforePrint(caller,function(data){
    			if(data.datas.length>1){   				
    					Ext.create('Ext.window.Window', {
    						autoShow: true,
    						title: '选择打印类型',
    						width: 400,
    						height: 300,
    						layout: 'anchor',
    						items: [{ 							    					
  							  anchor:'100% 100%',
  							  xtype:'form',
  							  id :'printbycondition',
  							  buttonAlign : 'center',
  							  items:[{
  							        xtype: 'combo',
  									id: 'template',
  									fieldLabel: '选择打印类型', 									
  									store: Ext.create('Ext.data.Store', {
  										autoLoad: true,
  									    fields: ['TITLE','ID','CONDITION','FILE_NAME'],
  									    data:data.datas 									 
  									}),
  									queryMode: 'local',
  								    displayField: 'TITLE',
  								    valueField: 'ID',
  									width:300,
  								    allowBlank:false,
  								    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
  									style:'margin-left:15px;margin-top:15px;',
  									listeners : {
									      afterRender : function(combo) {
									         combo.setValue(data.datas[0].ID);
									      }
									   }
  								}]	 							    	     				    							           	
  						 }], 
    						buttonAlign: 'center',
    						buttons: [{
    							text: '确定',
    							handler: function(b) {
    								var temp = Ext.getCmp('template');
    								if(temp &&  temp.value!= null){
    									var selData = temp.valueModels[0].data;
    									me.Print(caller,selData.FILE_NAME,selData.CONDITION);
    								}else{
    									alert("请选择打印模板");
    								}   								
    							}
    						}, {
    							text: '取消',
    							handler: function(b) {
    								b.ownerCt.ownerCt.close();
    							}
    						}]
    					});   					
    			}else{
    				me.Print(caller,data.datas[0].FILE_NAME,data.datas[0].CONDITION);
    			}    			
    		});
    	},
    	Print:function(caller,reportName,condition){
    		var me = this, form = me.ownerCt.ownerCt;
    		var id = Ext.getCmp(form.fo_keyField).value;  
    		if(!condition){
    			condition='{'+form.tablename.split(' ')[0]+'.'+form.keyField+'}='+id;
    		}
    		condition = condition.replace(/\[(.+?)\]/g,function(r){
  				var field=r.substring(1,r.length-1);
  				var da = Ext.getCmp(field);
  				if(da){
  					if(da.xtype=='textfield'){
  						return "'"+da.value+"'";
  					}else{
  						return da.value;
  					}
  				}else{
	    		   return '';
  				}
	    	});
	    	form.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + 'ma/printMT.action',
    			params:{
    				id:id,
    				caller:caller,
    				reportName:reportName,
    				condition:condition
    			},
    			timeout: 120000,
    			method:'post',
    			callback : function(options, success, response){
    				form.setLoading(false);
					var res = new Ext.decode(response.responseText);
					if(res.success){
						var url = res.info.printUrl + '?reportfile=' + res.info.reportname + '&&rcondition='+res.info.condition+'&&fdate=&&tdate=&&company=&&sysdate=373FAE331D06E956870163DCB2A96EC7&&key=3D7595A98BFF809D5EEEA9668B47F4A5&&whichsystem='+res.info.whichsystem+'';		
						window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + ',height=' + (window.screen.height*0.87) 
									+ ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
						if(Ext.getCmp('printbycondition')){
							Ext.getCmp('printbycondition').ownerCt.close();
						}
					}else{
			        	showError(res.exceptionInfo);
			        }
    			}
    		});
    	},
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});