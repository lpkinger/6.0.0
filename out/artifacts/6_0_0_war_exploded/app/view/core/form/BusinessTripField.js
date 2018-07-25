Ext.define('erp.view.core.form.BusinessTripField', {
	extend: 'Ext.form.Panel',
    alias: 'widget.businesstripfield',
    autoScroll:true,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    cls: 'u-form-default',
    minHeight: 220,
    keyField:'',
    title: '',
    tfnumber:0,
    bbar:[{}],
 	columns:[],
 	comboxs:'',
 	frame : true,
    initComponent: function() {
    	var me=this;
    	this.bbar=[{
    			text: '<font color="blue">+添加</font>'	,
    			handler: function(btn){
    	 			btn.ownerCt.ownerCt.addItem();
     			}
    	}];
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
    	Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params: {
				caller: 'datalistcombo',
				fields: 'dlc_value,dlc_display,dlc_fieldname',
				condition: "dlc_caller='"+caller+"'"
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				}
				if (r.success && r.data) {
					me.comboxs = new Ext.decode(r.data);
				}
			}
		});
    	me.getGridColumnsAndStore();
    	this.callParent(arguments);
    },
    
    layout:'column',
    setValue: function(value){
    	this.value = value;
    },
    listeners : {
    	afterrender: function(f){}
    },
    getGridColumnsAndStore:function(){
    	var me=this;
    	var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
    	var condition = urlCondition == null || urlCondition == "null" ? "" : urlCondition.replace(/IS/g, "=");
    	var gridParam = {caller: caller, condition: condition, _m: 0};
    	me.setLoading(true);
		
    	Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/singleGridPanel.action',
        	params: gridParam,
        	async: true,
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		if (!response) return;
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			me.columns=res.columns;
        			Ext.each(me.columns,function(c){
        				if(c.logic=='keyField') me.keyField=c.dataIndex;
        			});
        		}
        		if(!res.data || res.data.length == 2){
        			me.addItem();
        		}else {
            		data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		Ext.each(data,function(d){
                		Ext.each(me.columns,function(c){
                            var type='textfield',columnWidth=c.width/100,fieldLabel=c.text,value='',fieldStyle='',
                            	id=c.dataIndex+me.tfnumber,gridlogic=c.logic,readOnly=c.readOnly,editable=!readOnly,labelStyle='';
                            var field = Ext.getCmp(id);
                            if(!field){
                            	if(c.editor){
                                	type=c.editor.xtype;
                                }
                                if(c.width==0){
                                	type='hidden';
                                }
                                
                                if(readOnly){
                                	fieldStyle ='background:#e0e0e0;';
                                }
                                if(c.logic=='necessaryField'){
                                	labelStyle='color:#FF0000';
                                }
                    	        if(type=='combo'){
                    	        	var combox = new Array();
                    	        	for(var i=0;i<me.comboxs.length;i++){
                    	        		if(me.comboxs[i].DLC_FIELDNAME==c.dataIndex){
                    	        			combox.push(me.comboxs[i]);
                    	        		}
                    	        	}
                    				var store = Ext.create('Ext.data.Store', {
                    					 fields:['DLC_VALUE','DLC_DISPLAY'],   
                    					 data:combox
                    				});
                    				value=d[c.dataIndex];
                    				me.add({
                    					xtype: type,
                    					name: c.dataIndex,
                    					id: id,
                    					index:me.tfnumber,
                    					logic:'ignore',
                    					gridlogic:gridlogic,
                    					columnWidth: columnWidth,
                    					labelAlign: 'right',
                    					fieldLabel: fieldLabel,
                    					readOnly:readOnly,
                    					editable:editable,
                    					fieldStyle:fieldStyle,
                    					displayField: 'DLC_DISPLAY',
                    					valueField: 'DLC_VALUE',
                    					value:value,
                    					store:store,
                    					labelStyle:labelStyle
                    				});
                    			}else{
                    				value=d[c.dataIndex];
                    			    me.add({
                    			    	xtype: type,
                    					name: c.dataIndex,
                    					id: id,
                    					index:me.tfnumber,
                    					logic:'ignore',
                    					gridlogic:gridlogic,
                    					columnWidth: columnWidth,
                    					labelAlign: 'right',
                    					fieldLabel: fieldLabel,
                    					readOnly:false,
                    					editable:editable,
                    					fieldStyle:fieldStyle,
                    					value:value,
                    					labelStyle:labelStyle
                    				});
                    			}
                            }
                         });
                		var btn = Ext.getCmp('btn' + me.tfnumber);
                		if(!btn){
                			me.add(
                					{xtype: 'button',columnWidth: 0.08,text : '删除',gridlogic:'ignore',
                					name: 'btn'+ me.tfnumber,id: 'btn'+me.tfnumber,index: me.tfnumber,
                					style: 'margin-left : 10px;',
                					handler: function(btn){
                						var id=Ext.getCmp(me.keyField+btn.index).value;
                						if(id==''){
                							me.removeItems(btn);
                						}else{
                							me.deleteDetail(id,btn);
                						}
                					}
                				});	
                			me.add({
                				fieldLabel:'',
                				 width:120,
                				 columnWidth: 1,
                				 id:'lin'+me.tfnumber,
                				 boder:0,
                				 style:'padding-top:4px;padding-bottom:4px;background:none; border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 0px solid;',
                				 name:'blank'
                			})
                		}  
                		me.tfnumber=me.tfnumber+1;
            		});
            	}
        	}
        });
    },
    deleteDetail:function(id,delbtn){
    	var me=this;
    	warnMsg('是否确定删除', function(btn){
			if(btn == 'yes'){
				var url = "common/deleteDetail.action";
				me.setLoading(true);//loading...
				Ext.Ajax.request({
					url : basePath + url,
					params: {
						caller:caller,
						gridcaller: caller,
						condition: me.keyField + "=" + id
					},
					method : 'post',
					callback : function(options,success,response){
						me.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
			    			showError(localJson.exceptionInfo);return;
			    		}
						if(localJson.success){
							me.removeItems(delbtn);
						} else {
							delFailure();
			   			}
			   		}
				});
			}
		});
    },
    removeItems:function(btn){
    	var me=this;
    	Ext.each(me.columns,function(c){
		  	me.remove(Ext.getCmp(c.dataIndex+btn.index));
		    me.remove(Ext.getCmp('btn'+btn.index));
		    me.remove(Ext.getCmp('lin'+btn.index));
		});
    },
	addItem: function(item){
		var me=this;
		Ext.each(me.columns,function(c){
            var type='textfield',columnWidth=c.width/100,fieldLabel=c.text,value='',fieldStyle='',
            	id=c.dataIndex+me.tfnumber,gridlogic=c.logic,readOnly=c.readOnly,editable=!readOnly,labelStyle = '';
            var field = Ext.getCmp(id);
            if(!field){
            	if(c.editor){
                	type=c.editor.xtype;
                }
                if(c.width==0){
                	type='hidden';
                }
                if(type=='dbfindtrigger'){
	            	readOnly=false;
	            }
                if(readOnly){
                	fieldStyle ='background:#e0e0e0;';
                }
                if(c.logic=='necessaryField'){
                	labelStyle='color:#FF0000';
                }
    	        if(type=='combo'){
    				var com = new Array();
    	        	for(var i=0;i<me.comboxs.length;i++){
    	        		if(me.comboxs[i].DLC_FIELDNAME==c.dataIndex){
    	        			com.push(me.comboxs[i]);
    	        		}
    	        	}
    				var store = Ext.create('Ext.data.Store', {
    					 fields:['DLC_VALUE','DLC_DISPLAY'],   
    					 data:com
    				});
    				me.add({
    					xtype: type,
    					name: c.dataIndex,
    					id: id,
    					index:me.tfnumber,
    					logic:'ignore',
    					gridlogic:gridlogic,
    					columnWidth: columnWidth,
    					labelAlign: 'right',
    					fieldLabel: fieldLabel,
    					readOnly:readOnly,
    					editable:editable,
    					fieldStyle:fieldStyle,
    					displayField: 'DLC_DISPLAY',
    					valueField: 'DLC_VALUE',
    					store:store,
    					labelStyle:labelStyle
    				});
    				
    			}else{
    			 me.add({
    				xtype: type,
					name: c.dataIndex,
					id: id,
					index:me.tfnumber,
					logic:'ignore',
					gridlogic:gridlogic,
					columnWidth: columnWidth,
					labelAlign: 'right',
					fieldLabel: fieldLabel,
					readOnly:false,
					editable:editable,
					fieldStyle:fieldStyle,
					value:value,
					labelStyle:labelStyle
    				});
    			}
            }
         });
		var btn = Ext.getCmp('btn' + me.tfnumber);
		if(!btn){
			me.add(
					{xtype: 'button',columnWidth: 0.08,text : '删除',gridlogic:'ignore',
					name: 'btn'+ me.tfnumber,id: 'btn'+me.tfnumber,index: me.tfnumber,
					style: 'margin-left : 10px;',
					handler: function(btn){
						var id=Ext.getCmp(me.keyField+btn.index).value;
						if(id==''){
							me.removeItems(btn);
						}else{
							me.deleteDetail(id,btn);
						}
					}
				});	
			me.add({
				fieldLabel:'',
				 width:120,
				 columnWidth: 1,
				 id:'lin'+me.tfnumber,
				 boder:0,
				 style:'padding-top:4px;padding-bottom:4px;background:none; border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 0px solid;',
				 name:'blank'
			})
		}
		            			
		me.tfnumber=me.tfnumber+1;
	}
});