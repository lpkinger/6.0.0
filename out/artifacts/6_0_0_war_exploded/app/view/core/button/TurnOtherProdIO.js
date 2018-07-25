/**
  * 一种出入库单转为另一种出入库单
 */	
Ext.define('erp.view.core.button.TurnOtherProdIO',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnOtherProdIOButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnOtherProdIOButton,
    	style: {
    		'margin-left': '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
			afterrender:function(btn){
				var status = Ext.getCmp("pi_statuscode");
				if(status && status.value != 'POSTED'){
					btn.hide();
				}
			}
		},
		handler:function(){
			var me = this;
			if(!me.win){
				Ext.Ajax.request({
                    url: basePath + '/pm/bom/getDescription.action',
                    params: {
                        tablename: "documentsetup",
                        field: 'ds_inorout',
                        condition: "ds_name='" + Ext.getCmp("pi_class").value + "'",
                        caller: caller
                    },
                    method: 'post',
                    callback: function(options, success, response) {
                        var res = new Ext.decode(response.responseText);
                        if (res.exceptionInfo) {
                            showError(rs.exceptionInfo);
                        } else {
                            var inOrOut = res.description;
                            var form = null;
                            if (inOrOut == "IN" || inOrOut == "-OUT") {
                                form = new Ext.form.Panel({
        					    	xtype:'form',
        					    	anchor:'100% 100%',
        					    	layout: 'absolute',
        					    	items:[{
        				                xtype:"combo",
        				                y:25,
        				                fieldLabel:'转入单据类型',
        				                labelWidth:120,
        				                store:{
        				                    fields:[
        				                        'display',
        				                        'value'
        				                    ],
        				                    data:[{
    				                            display:"出货单",
    				                            value:"出货单"
    				                        },
    				                        {
    				                            display:"借货出货单",
    				                            value:"借货出货单"
    				                        },
    				                        {
    				                            display:"委外补料单",
    				                            value:"委外补料单"
    				                        },
    				                        {
    				                            display:"报废单",
    				                            value:"报废单"
    				                        },
    				                        {
    				                            display:"委外领料单",
    				                            value:"委外领料单"
    				                        },
    				                        {
    				                            display:"生产补料单",
    				                            value:"生产补料单"
    				                        },
    				                        {
    				                            display:"生产领料单",
    				                            value:"生产领料单"
    				                        },
    				                        {
    				                            display:"其它出库单",
    				                            value:"其它出库单"
    				                        },
    				                        {
    				                            display:"其它采购出库单",
    				                            value:"其它采购出库单"
    				                        },
    				                        {
    				                            display:"拨出单",
    				                            value:"拨出单"
    				                        },
    				                        {
    				                        	display:"维修出库单",
    				                            value:"维修出库单"
    				                        }
        				                    ]
        				                },
        				                queryMode:"local",
        				                displayField:"display",
        				                valueField:"value",
        				                editable:false,
        				                minValue:null,
        				                maxLength:4000,
        				                positiveNum:false
        				            }]
        					    });
                            } else if (inOrOut == "-IN" || inOrOut == "OUT") {
                            	form = new Ext.form.Panel({
        					    	xtype:'form',
        					    	anchor:'100% 100%',
        					    	items:[{
        				                xtype:"combo",
        				                columnWidth:0.6,
        				                style:{
        				                	'margin-top':'10px'
        				                },
        				                fieldLabel:'转入单据类型',
        				                store:{
        				                    fields:[
        				                        'display',
        				                        'value'
        				                    ],
        				                    data:[{
    				                            display:"销售退货单",
    				                            value:"销售退货单"
    				                        },
    				                        {
    				                            display:"借货归还单",
    				                            value:"借货归还单"
    				                        },
    				                        {
    				                            display:"委外退料单",
    				                            value:"委外退料单"
    				                        },
    				                        {
    				                            display:"生产退料单",
    				                            value:"生产退料单"
    				                        },
    				                        {
    				                            display:"其它入库单",
    				                            value:"其它入库单"
    				                        },
    				                        {
    				                            display:"其它采购入库单",
    				                            value:"其它采购入库单"
    				                        },
    				                        {
    				                            display:"拨入单",
    				                            value:"拨入单"
    				                        }
        				                    ]
        				                },
        				                queryMode:"local",
        				                displayField:"display",
        				                valueField:"value",
        				                editable:false,
        				                minValue:null,
        				                maxLength:4000,
        				                positiveNum:false
        				            },{
        				            	xtype:'checkbox',
        				            	fieldLabel:'只转条码信息',
        				            	listeners:{
        				            		change:function(field,newValue){
        				            			if(newValue){
        				            				field.ownerCt.items.items[2].show();
        				            			}else{
        				            				field.ownerCt.items.items[2].hide();
        				            			}
        				            		}
        				            	}
        				            },{
        				            	xtype:'textfield',
        				            	columnWidth:0.6,
        				            	fieldLabel:'条码转入单号',
        				            	value:'',
        				            	hidden:true
        				            }]
        					    });
                            }
                            me.win = new Ext.window.Window({
        			    		id : 'wins',
        					    height: "30%",
        					    width: "30%",
        					    title:'转其它单据',
        					    maximizable : false,
        						buttonAlign : 'center',
        						layout : 'anchor',
        						closeAction:'hide',
        					    items: [form],
        					    buttons : [{
        					    	name: 'confirm',
        					    	text : $I18N.common.button.erpConfirmButton,
        					    	iconCls: 'x-button-icon-confirm',
        					    	cls: 'x-btn-gray',
        					    	listeners: {
        	   				    		click: function(btn) {
        	   				    			var form = btn.ownerCt.ownerCt.down('form');
        	   				    			var pi_class = form.items.items[0].value;
        	   				    			if(pi_class==null||pi_class==''){
    	   				    	        		showError('请选择转入单据!');
    	   				    	        		return;
    	   				    	        	}
        	   				    			if(form.items.items[1]&&form.items.items[1].getValue()){
        	   				    				var pi_inoutno = form.items.items[2].value;
        	   				    				if(pi_inoutno==''||pi_inoutno==null){
        	   				    					showError('已勾选只转条码信息,请填写条码转入单号!');
        	   				    	        		return;
        	   				    				}
        	   				    			}
        	   				    			me.turn(pi_class,pi_inoutno);
        	   				    		}
        					    	}
        					    }, {
        					    	text : $I18N.common.button.erpCloseButton,
        					    	iconCls: 'x-button-icon-close',
        					    	cls: 'x-btn-gray',
        					    	handler : function(btn){
        					    		btn.ownerCt.ownerCt.close();
        					    	}
        					    }]
        					});
                            me.win.show();
                        }
                    }
                });
			}else{
				me.win.show();
			}
        },
        turn:function(pi_class,pi_inoutno){
        	var me = this;
        	me.setLoading(true);
        	Ext.Ajax.request({
                url: basePath + 'scm/reserve/turnOtherProdIO.action',
                params: {
                	pi_class: pi_class||'',
                	pi_id: Ext.getCmp('pi_id').value,
                	pi_inoutno:pi_inoutno||'',
                	caller:caller
                },
                method: 'post',
                callback: function(options, success, response) {
                	me.setLoading(false);
                    var res = new Ext.decode(response.responseText);
                    if (res.exceptionInfo) {
                        showError(res.exceptionInfo);
                        return;
                    } else if (res.success) {
                    	if(res.log){
							showMessage("提示", res.log);
						}
                    	Ext.Msg.alert("提示", "处理成功!");
                    	window.location.reload();
                    }
                }
            });
        },
        setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
    		var mask = this.mask;
    		if (!mask) {
    			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
    				msg : "处理中,请稍后...",
    				msgCls : 'z-index:10000;'
    			});
    		}
    		if (b)
    			mask.show();
    		else
    			mask.hide();
    	},
	});