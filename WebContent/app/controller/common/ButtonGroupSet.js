Ext.QuickTips.init();
Ext.define('erp.controller.common.ButtonGroupSet', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    	'common.main.Toolbar', 'core.trigger.SearchField', 'common.ButtonGroupSet','common.ButtonGroupSetPanel'
    ],
    init:function(){
    	var me = this;
    	this.control({
            'ButtonGroupSetPanel': {
            	afterrender: function(f) {
            		var me = this;
            		var data = me.getButtonGroupSet();
        			me.setSysGroup(data);
            	},
            	addDragListener: function(view) {
            		var d = new Ext.dd.DragZone(view.el, {
						ddGroup: 'moduleitem',
						containerScroll : true,
						onBeforeDrag: function(data, e) {
							if(data.config.xtype.indexOf('Print')>-1){
								return false;
							}
							return !data.config.group;
						},
						getDragData: function(e) {
				            var sourceEl = e.getTarget(view.itemSelector, 10), d;
				            if (sourceEl) {
				                d = sourceEl.cloneNode(true);
				                d.id = Ext.id();
				                var record = view.getRecord(sourceEl);
				                return (view.dragData = {
				                    sourceEl: sourceEl,
				                    repairXY: Ext.fly(sourceEl).getXY(),
				                    ddel: d,
				                    config: {
				                    	caller: record.get('caller'),
				                    	groupid: record.get('groupid'),
				                    	index: sourceEl.viewIndex,
				                    	xtype: record.get('_xtype'),
				                    	text: record.get('text'),
				                    	group: record.get('group')
				                    }
				                });
				            }
				        },
				        getRepairXY: function() {
				            return this.dragData.repairXY;
				        },
				        onStartDrag: function() {
				        	Ext.fly(this.dragData.sourceEl).addCls('x-target-drag');
				        },
				        afterInvalidDrop: function() {
				        	Ext.fly(this.dragData.sourceEl).removeCls('x-target-drag');
				        },
				        afterValidDrop: function() {
				        	Ext.fly(this.dragData.sourceEl).removeCls('x-target-drag');
				        }
					});
					view.dragZone = d;
            	},
            	addDropListener: function(view){
            		var drop = new Ext.dd.DropZone(view.el, {
						ddGroup:'moduleitem',
					    getTargetFromEvent: function(e) {
				            return e.getTarget('.x-module-item');
				        },
				        onNodeEnter : function(target, dd, e, data){
				            //Ext.fly(target).addCls('x-target-hover');
				        },
				        onNodeOut : function(target, dd, e, data){
				            Ext.fly(target).removeCls('x-target-hover-below');
				            Ext.fly(target).removeCls('x-target-hover-above');
				        },
				        onNodeOver : function(target, dd, e, data){
				        	Ext.fly(target).removeCls('x-target-hover-below');
				            Ext.fly(target).removeCls('x-target-hover-above');
				        	var exy = e.getXY(),
				        		tbox = target.getBoundingClientRect(),
				        		trecord = view.getRecord(target);
				        		
				        	if(exy[1] >= (tbox.top+tbox.height/2)) { // 鼠标在目标项下半区域
				        		data.config.pos = 'below';
				        		Ext.fly(target).addCls('x-target-hover-below');
				        	}else { // 鼠标在目标项上半区域
				        		// 不允许添加到第一个项目之前
				        		if(target.viewIndex == 0) {
				        			data.config.pos = 'below';
				        			Ext.fly(target).addCls('x-target-hover-below');
				        			return Ext.dd.DropZone.prototype.dropAllowed;
				        		}else {
				        			data.config.pos = 'above';
				        			Ext.fly(target).addCls('x-target-hover-above');
				        		}
				        	}
				        	if(trecord.get('group')) {
				        		data.config.groupid = trecord.get('id');
				        	}else {
				        		data.config.groupid = trecord.get('groupid');
				        	}
				        	if(data.config.group) {
				        		return Ext.dd.DropZone.prototype.dropNotAllowed;
				        	}else
				            	return Ext.dd.DropZone.prototype.dropAllowed;
				        },
				        onNodeDrop : function(target, dd, e, data){
				            var store = view.getStore(),
				            	targetIndex = target.viewIndex,
				            	originIndex = data.config.index,
				            	pos = data.config.pos,
				            	index = pos=='below'?(targetIndex + 1):(targetIndex),
				            	groupid = view.getRecord(target).get('groupid'),
				            	newItem = {
				            		caller: data.config.caller,
				            		_xtype: data.config.xtype,
				            		index: originIndex,
				            		text: data.config.text,
				            		group: data.config.group,
				            		groupid: groupid
				            	};
				            if(typeof originIndex === 'number') { // 从centerpanel调整顺序
				            	if(originIndex == targetIndex) {
				            		return true;
				            	}else if(originIndex > targetIndex) {
				            		store.removeAt(originIndex);
				            		
				            		store.insert(index, newItem);
				            	}else {
				            		store.insert(index, newItem);
				            		store.removeAt(originIndex);
				            	}
				            }
				            // 重设序号
				            store.data.each(function(d,i){if(d.get('index')>=index){d.set('index', i);}});
				        }
					});
					view.dropZone = drop;	
            	},
            	resize: function(view, adjWidth, adjHeight, eOpts) {
            		var centerPanel = Ext.getCmp('dataviewpanel');
            		view.resetViewSize(centerPanel.getWidth(), centerPanel.getHeight());
            	},
            	reSet: function(){
					warnMsg("确定要还原该单据的按钮设置吗？", function(btn){
						if(btn == 'yes'){
							Ext.getBody().mask('waiting...')
		            		Ext.Ajax.request({
		            			url: basePath + 'crm/deleteButtonGroup.action',
		            			method: 'post',
		                        params: {
		                        	caller: caller
		                        },
		                        callback: function(options, success, response) {
		                        	Ext.getBody().unmask();
		                        	var res = new Ext.decode(response.responseText);
		                        	if(res.success) {
		                        		showMessage('提示', '还原成功!', 3000);
		                        	}else {
		                        		showError(res.exceptionInfo);
		                        	}
		                        }
		            		});
						} else {
							return;
						}
					});
            	},
            	onSave: function(panel) {
            		var data = panel.getAllGroup();
            		Ext.getBody().mask('waiting...')
            		Ext.Ajax.request({
            			url: basePath + 'crm/saveButtonGroup.action',
            			method: 'post',
                        params: {
                        	caller: caller,
                            jsonstr: Ext.JSON.encode(data)
                        },
                        callback: function(options, success, response) {
                        	Ext.getBody().unmask();
                        	var res = new Ext.decode(response.responseText);
                        	if(res.success) {
                        		showMessage('提示', '保存成功!', 3000);
                        	}else {
                        		showError(res.exceptionInfo);
                        	}
                        }
            		});
            	}
            }
    	});
    },
    getButtonGroupSet:function(){
    	var data = [];
    	Ext.Ajax.request({
			method: 'post',
            url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'BGS_DETNO,BGS_XTYPE,BGS_NAME,BGS_GROUP,BGS_CALLER,BGS_GROUPID',
				caller : 'BUTTONGROUPSET',
				condition : 'BGS_CALLER = \''+ caller +'\''
			},
            callback: function(options, success, response) {
            	var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					data.push({
						index:item.BGS_DETNO,
						_xtype:item.BGS_XTYPE,
						text:item.BGS_NAME,
						groupname:item.BGS_GROUP,
						groupid:item.BGS_GROUPID
					});
				});
            }
		});
		return data;
    },
    setSysGroup:function(loadData){
    	//固定分组
		var base_group = ['erpAddButton','erpUpdateButton','erpDeleteButton','erpSaveButton','erpCopyButton','erpExecuteOperationButton','erpQueryButton'],
		logic_group = ['erpSubmitButton','erpResSubmitButton','erpAuditButton','erpResAuditButton','erpEndButton','erpResEndButton',
             'erpAccountedButton','erpResAccountedButton','erpPostButton','erpResPostButton','erpBannedButton','erpResBannedButton',
             'erpForBiddenButton','erpResForBiddenButton','erpAutoInvoiceButton','erpCheckButton','erpResCheckButton','erpVoCreateButton',
             'erpFreezeButton','erpNullifyButton','erpResAbateButton','erpAbateButton','erpModifyCommonButton'],
		work_group = ['erpExportExcelButton','erpImportExcelButton'],
		turn_group = ['erpConsignButton'],
		close_group = ['erpCloseButton'];
		if(loadData.length>0){
			var base_group = [],logic_group = [],work_group = [],turn_group = [],close_group = [];
	    	//读取分组
	    	Ext.Array.each(loadData,function(item,index){
	    		switch(item.groupid){
					case '1':
					  base_group.push(item._xtype)
					  break;
					case '2':
					  logic_group.push(item._xtype)
					  break;
					case '3':
					  turn_group.push(item._xtype)
					  break;
					case '4':
					  work_group.push(item._xtype)
					  break;
					case '5':
					  close_group.push(item._xtype)
					  break;
					default:
					  break;
				}
	    	});
		}
		//读取Form设置
		var btnArr = button4rw.split('#');
		base_group = base_group.filter(function(v){
			return btnArr.indexOf(v)>-1
		});
		logic_group = logic_group.filter(function(v){
			return btnArr.indexOf(v)>-1
		});
		work_group = work_group.filter(function(v){
			return btnArr.indexOf(v)>-1
		});
		close_group = close_group.filter(function(v){
			return btnArr.indexOf(v)>-1
		});
		turn_group = turn_group.filter(function(v){
			return btnArr.indexOf(v)>-1
		});
		var allButton = union_array(base_group,logic_group);//已分组的所有按钮
		allButton = union_array(allButton,work_group);
		allButton = union_array(allButton,close_group);
		var otherButton = btnArr.filter(function(v){ 
			return !(allButton.indexOf(v) > -1) 
		}).concat(allButton.filter(function(v){
			return !(btnArr.indexOf(v) > -1)}
		));
		turn_group = union_array(turn_group,otherButton);
		//初始化数据
		var datas = new Array();
		datas.push(base_group);
		datas.push(logic_group);
		datas.push(turn_group);
		datas.push(work_group);
		datas.push(close_group);
		var groups = new Array();
		Ext.Array.each(datas,function(data,index){
			var group = {};
			group.group = true
			switch(index)
			{
				case 0:
				  group.groupid = 1;
				  group.name = '基本操作组';
				  break;
				case 1:
				  group.groupid = 2;
				  group.name = '逻辑功能组';
				  break;
				case 2:
				  group.groupid = 3;
				  group.name = '更多操作组';
				  break;
				case 3:
				  group.groupid = 4;
				  group.name = '工作业务组';
				  break;
				case 4:
				  group.groupid = 5;
				  group.name = '关闭组';
				  break;
				default:
				  break;
			}
			var buttons = new Array();
			Ext.Array.each(data,function(item){
				var b = {
					caller:caller,
					_xtype:item,
					text: $I18N.common.button[item]?$I18N.common.button[item]:item,
					xtype: 'button',
					groupid: group.groupid
				}
				Ext.Array.each(loadData,function(data){
					if(b._xtype==data._xtype){
						b.text = data.text;
					}
				})
				buttons.push(b);
			});
			group.items = buttons;
			groups.push(group);
		});
		Ext.getCmp('ButtonGroupSetPanel').importGroupData(groups);
    }
});