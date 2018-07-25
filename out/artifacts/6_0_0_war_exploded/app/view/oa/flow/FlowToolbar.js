/**
 * 流程界面toolbar基类
 */
Ext.define('erp.view.oa.flow.FlowToolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.FlowToolbar',
	dock : 'top',
	requires : [ 'erp.view.oa.flow.button.TurnButton','erp.view.oa.flow.button.SubmitButton',
				 'erp.view.oa.flow.button.SaveButton'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	padding:'4 5 0 5',
	cls:'x-flow-tbar',
	style:'border-bottom:1px solid #d0d0d0;',
	initComponent : function() {
		var me = this;
		var items;
		//第一个标签页的toolbar
		if(me._first){
			items = me.loadButtons();
			//权限控制按钮
			items = me.setButtonsPower(items);
		}
		//jsp定义的参数  是否使用按钮
		if(usingButton){
			items = null;
		}
		//新增时的toolbar
		if(me._add){
			me.style='border:1px solid #d0d0d0;';
			items = [{
				height:22,
				xtype:'SubmitButton',
				margin:'0 5 0 0'
			},{
				height:22,
				xtype:'button',
				margin:'0 5 0 0',
				cls:'x-btn-gray',
				text:'保存到草稿',
				_url:status=='text'?'common/updateCommon.action':'common/saveCommon.action',
				handler:function(btn){
					var form = btn.ownerCt.ownerCt;
					form.beforeSave(form,btn._url);
				}
			},{
				height:22,
				xtype:'button',
				cls:'x-btn-gray',
				text:'关闭',
				margin:'0 5 0 0',
				handler:function(){
					var errInfo = '单据未提交，是否继续关闭？';
					warnMsg(errInfo, function(btn){
						if(btn == 'yes'){
							me.FormUtil.beforeClose(me);
						} else {
							return;
						}
					});
				}
			}];
		}
		Ext.apply(this, {
			items : items
		});
		this.callParent(arguments);
	},
	loadButtons: function() {
		var arr = [];
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var id = formCondition.split('=')[1];
		Ext.Ajax.request({
			url: basePath + "oa/flow/getOperation.action",
			async: false,
			params: {
				nodeId: nodeId,
				caller: caller,
				id: id
			},
			callback: function(option, success, response){
				var res = Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return false;
				}
				var data = res.data;
				Ext.each(data, function(d,index){
					var s = new Object();
					s.xtype = 'TurnButton';
					s.text = d.FO_NAME,
					s.margin = '0 5 0 0',
					s.type = d.FO_TYPE
					s.group = {
						name : d.FO_GROUPNAME
					}
					s.foname = d.FO_NAME;
					s.foid = d.FO_ID;
					s.version = d.FO_FDSHORTNAME,
					s.isduty = d.FO_ISDUTY,
					arr.push(s);
				});
			}
		});
		return arr;
	},
	setButtonsPower: function(buttons) {
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var id = formCondition.split('=')[1];
		Ext.Ajax.request({
			url: basePath + "oa/flow/getRole.action",
			async: false,
			params: {
				nodeId: nodeId,
				caller: caller,
				id:id
			},
			callback: function(option, success, response){
				var res = Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return false;
				}
				var duty,actor,reader,creator,hadCheck=false;
				Ext.each(res.data, function(data,index){
					if(data.type=='duty'){
						duty = true;
						if(data.confirm=='1'){
							hadCheck = true;
						}
					}
					if(data.type=='actor'){actor = true;}
					if(data.type=='reader'){reader = true;}
					if(data.type=='creator'){creator = true;}
				});
				if(hadCheck){duty=false,actor=false}
				//按钮分2种显示    1.turn task flow(责任人)  2.update（参与者）  --读者和创建者无按钮  3.按钮本身isduty 是=责任人可操作 否=参与者可操作
				if(duty&&!actor){
					Ext.each(buttons, function(button,index){
						if(button.type=='Update'&&!(button.isduty=='true')){
							button.hidden = true;
						}
					});
				}else if(!duty&&actor){
					Ext.each(buttons, function(button,index){
						if(button.type!='Update'||button.isduty=='true'){
							button.hidden = true;
						}
					});
				}else if(duty&&actor){
					buttons = buttons;
				}else if(!duty&&!actor){
					buttons = null;
				}
			}
		});
		return buttons;
	}
});