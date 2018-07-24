Ext.define('erp.view.core.form.CustomerSelectField', {
	extend: 'Ext.form.Panel',
    alias: 'widget.customerselectfield',
    autoScroll:true,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    cls: 'u-form-default',
    minHeight: 220,
    keyField:'',
    title: '',
    tfnumber:0,
    bbar:[{text: '<font color="blue">+添加客户名称、客户地址</font>',
 			handler: function(btn){
	 			btn.ownerCt.ownerCt.addItem();
 			}
 	}],
 	columns:[],
 	frame : true,
    tfnumber: 0,
    initComponent: function() {
    	var me=this;
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
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
            				id=c.dataIndex+me.tfnumber,gridlogic=c.logic,readOnly=c.readOnly,editable=!readOnly;
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
            				value=d[c.dataIndex];
	            			me.add({
								xtype: type,
								index:me.tfnumber,
								name: c.dataIndex,
								id: id,
								logic:'ignore',
								gridlogic:gridlogic,
								readOnly:readOnly,
								editable:editable,
								columnWidth: columnWidth,
								labelAlign: 'left',
								fieldStyle:fieldStyle,
								fieldLabel: fieldLabel,
								value:value
							});
            			});
            			me.add({xtype: 'button',columnWidth: 0.08,text : '删除',gridlogic:'ignore',
            			name: 'btn' + me.tfnumber,id: 'btn' + me.tfnumber,index: me.tfnumber,
            			style: 'margin-left : 10px;',
            			handler: function(btn){
					        	var id=Ext.getCmp(me.keyField+btn.index).value;
					        	if(id==''){
					        		me.removeItems(btn);
					        	}else{
					        		me.deleteDetail(id,btn);
					        	}	 			
	 			    	}});	            			
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
		});
    },
	addItem: function(item){
		var me=this;
		Ext.each(me.columns,function(c){
            var type='textfield',columnWidth=c.width/100,fieldLabel=c.text,value='',fieldStyle='',
            	id=c.dataIndex+me.tfnumber,gridlogic=c.logic,readOnly=c.readOnly,editable=!readOnly;
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
	        me.add({
				xtype: type,
				name: c.dataIndex,
				id: id,
				index:me.tfnumber,
				logic:'ignore',
				gridlogic:gridlogic,
				columnWidth: columnWidth,
				labelAlign: 'left',
				fieldLabel: fieldLabel,
				readOnly:readOnly,
				editable:editable,
				fieldStyle:fieldStyle,
				value:value
				});
         });
		me.add({xtype: 'button',columnWidth: 0.08,text : '删除',gridlogic:'ignore',
            	name: 'btn' + me.tfnumber,id: 'btn' + me.tfnumber,index: me.tfnumber,
            	style: 'margin-left : 10px;',
          		handler: function(btn){
		        	var id=Ext.getCmp(me.keyField+btn.index).value;
		        	if(id==''){
		        		me.removeItems(btn);
		        	}else{
		        		me.deleteDetail(id,btn);
		        	}
	 			}});	            			
	            me.tfnumber=me.tfnumber+1;
	}
});