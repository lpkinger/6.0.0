Ext.define('erp.view.core.button.TaskTransfer', {
	extend : 'Ext.Button',
	alias : 'widget.erpTaskTransferButton',
	iconCls : 'x-button-icon-add',
	cls : 'x-btn-gray',
	id : 'addbtn',
	text : $I18N.common.button.erpTaskTransferButton,
	style : {
		marginLeft : '10px'
	},
	width : 100,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners:{
		afterrender:function(){
			var me = this;
			Ext.getCmp('query').handler = function(){
				if(!Ext.getCmp('resourcecode').value){
					showMessage('提示','请先选择任务人',1500);
					return;
				}
				this.ownerCt.ownerCt.onQuery();
			};
			var form = Ext.getCmp('dealform');
			form.getCondition = function(){
				return me.getCondition(form);
			}
			form.addManagedListener(form,'alladded',function(){
				form.add([{
					xtype:'dbfindtrigger',
					fieldLabel:'移交人',
					dataIndex:'ra_turnemname',
					name: "ra_turnemname",
      				id: "ra_turnemname",
					columnWidth:0.25, 
					cls:"form-field-allowBlank",
					fieldStyle: "background:#E0E0FF;color:#515151;",
					editable: true,
					allowBlank: false,
					labelAlign: "left"					
				},{
					xtype:'textfield',
					fieldLabel:'移交人编号',
					dataIndex:'ra_turnemcode',
					name: "ra_turnemcode",
      				id: "ra_turnemcode",
					columnWidth:0.25, 
					cls:"form-field-allowBlank",
					fieldStyle: "background:#FFFAFA;color:#515151;",
					editable: true,
					readOnly:true,
					allowBlank: true,
					labelAlign: "left"
				}]);
				
				var resourceTrigger = Ext.getCmp('resourcename');
				if(resourceTrigger){
					resourceTrigger.addManagedListener(resourceTrigger,'aftertrigger',function(trigger,data){
						form.onQuery();
					});					
				}
			});
		}
	},
	handler:function(){
		var grid = Ext.getCmp('batchDealGridPanel');
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		var bool = true;
        		Ext.each(grid.multiselected, function(){
        			if(this.data[grid.keyField] == item.data[grid.keyField]){
        				bool = false;
        			}
        		});
        		if(bool){
        			grid.multiselected.push(item);
        		}
        	}
        });
        var records = grid.multiselected;
        if(records.length > 0){
        	this.confirm(records,grid,this);
        }else{
        	showMessage('提示',"请勾选需要的明细!",1500);
        }
	},
	confirm:function(records,grid,btn){
		var from = Ext.getCmp('resourcecode');
		if(!from.value){
			Ext.Msg.alert('提示','原任务执行人编号不能为空!');
			return;
		}
		var to = Ext.getCmp('ra_turnemcode');
		if(!to.value){
			Ext.Msg.alert('提示','移交人编号不能为空!');
			Ext.defer(function(){
				to.focus();
			},500);
			return;
		}
		var fromEm = from.value;
		var toEm = to.value;
		if(fromEm==toEm){
			Ext.Msg.alert('提示','移交人不能与原执行人相同!');
			return;
		}
		var ids = new Array();
		var val;
		Ext.Array.each(records,function(record){
			val = record.get('id')||record.get('ID');
			if(val){
				ids.push(val);
			}
		});
		var idStr = ids.join(',');
		
		if(idStr){
			Ext.Msg.confirm('确认','确定将任务移交给<font color="red">'+Ext.getCmp('ra_turnemname').value+'('+toEm+')'+'</font>',function(btn){
				if(btn=='yes'){
					Ext.Ajax.request({
						url:basePath + 'plm/record/taskTransfer.action',
						method:'post',
						params:{
							ids:idStr,
							to:toEm,
							from:fromEm
						},
						callback:function(options,sucess,response){
							var res = Ext.decode(response.responseText);
							if(res.success){
								Ext.Msg.alert('提示','移交成功!');
								var form = Ext.getCmp('dealform');
								form.onQuery();
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
							
						}
					});					
				}
			});
		}
	},
	getCondition: function(form){ //由于projecttask的资源编号是连在一起用逗号分隔的，因此对应资源编号需要用like来实现查找
		var grid =  Ext.getCmp('batchDealGridPanel');
		if(!grid){
			grid = Ext.getCmp('grid');
		}
		var condition = typeof grid.getCondition === 'function' ? grid.getCondition(true) : 
			(Ext.isEmpty(grid.defaultCondition) ? '' : ('(' + grid.defaultCondition + ')'));
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if((f.xtype == 'checkbox' || f.xtype == 'radio')){
					if(f.value == true) {
						if(condition == ''){
							condition += f.logic;
						} else {
							condition += ' AND ' + f.logic;
						}
					}
				} else if(f.xtype == 'datefield' && f.value != null && f.value != '' && !contains(f.logic, 'to:', true)){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += "to_char("+f.logic+",'yyyy-MM-dd')='"+v+"'";
					} else {
						condition += " AND to_char("+f.logic+",'yyyy-MM-dd')='"+v+"'";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != '' && !contains(f.logic, 'to:', true)){
					var endChar = f.logic.substr(f.logic.length - 1);
					if(endChar != '>' && endChar != '<')
						endChar = '=';
					else
						endChar = '';
					if(condition == ''){
						condition += f.logic + endChar + f.value;
					} else {
						condition += ' AND ' + f.logic + endChar + f.value;
					}
				} else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						var _a = '';
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(_a == ''){
									_a += f.logic + "='" + d.data.value + "'";
								} else {
									_a += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += _a + ')';
					}
				} else if(f.xtype=='adddbfindtrigger' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + ' in (' ;		
					} else {
						condition += ' AND ' + f.logic + ' in (';
					}
					var str=f.value,constr="";
					for(var i=0;i<str.split("#").length;i++){
						if(i<str.split("#").length-1){
							constr+="'"+str.split("#")[i]+"',";
						}else constr+="'"+str.split("#")[i]+"'";
					}
					condition +=constr+")";
				} else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(!Ext.isEmpty(f.value)){
							if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value.toString(), '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else if(f.value.toString().charAt(0) == '!'){ 
								if(condition == ''){
									condition += 'nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "'";
								} else {
									condition += ' AND (nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "')";
								}
							} else {
								if(f.value.toString().indexOf('%') >= 0) {
									if(condition == ''){
										condition += f.logic + " like '" + f.value + "'";
									} else {
										condition += ' AND (' + f.logic + " like '" + f.value + "')";
									}
								} else {
									if(f.dataIndex.toUpperCase()=='RESOURCECODE'||f.dataIndex.toUpperCase()=='RESOURCENAME'){
										if(condition == ''){
											condition += f.logic + " like '%" + f.value + "%'";
										} else {
											condition += ' AND (' + f.logic + " like '%" + f.value + "%')";
										}										
									}else{
										if(condition == ''){
											condition += f.logic + "='" + f.value + "'";
										} else {
											condition += ' AND (' + f.logic + "='" + f.value + "')";
										}										
									}
								}
							}
						}
					}
				}
			}
		});
		return condition;
	}
});