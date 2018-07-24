Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.MeetingRoom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.meeting.MeetingRoom','core.form.Panel','core.trigger.MultiDbfindTrigger','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.form.YnField','core.trigger.DbfindTrigger','common.query.Form'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpDatalistGridPanel': {
    			afterrender: function(grid){
    				grid.onGridItemClick = function(){//改为点击button进入详细界面
    					me.onGridItemClick(grid.selModel.lastSelected);
    				};
    			}
    		},
    		'button[id=delete]': {
    			click: function(){
    				me.vastDelete();
    			}
    		},
    		'button[id=add]': {
    			click: function(){
    				me.newMeetingRoom();
    			}
    		},
    		'button[name=query]': {
    			click: function(){
    				var form = Ext.getCmp('queryform');
    				var condition = form.spellCondition('');//传条件，已提交、已审核等，默认空。
    				var grid = Ext.getCmp('grid');
    				grid.getCount('MeetingRoom', condition);//queryform grid caller不同，所以datalist：MeetingRoom
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	newMeetingRoom: function(){
    	var win = new Ext.window.Window({
			id : 'win',
			title: "新增会议室",
			height: "80%",
			width: "70%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/meeting/new.jsp" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
    	win.show();	
    },
    onGridItemClick: function(record){
    	var me = this;
    	var id = record.data.mr_id;    	
    	var win = new Ext.window.Window({
			id : 'win',
			title: "会议室设置",
			height: "90%",
			width: "70%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 
				'jsps/oa/meeting/meetingRoomInfo.jsp?formCondition=mr_idIS' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
    	win.show();
    }, 
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    },
    getCurrentStore: function(value){
    	var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data[keyField];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data[keyField];
			}
			var v = item.data[keyField];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
    },
    vastDelete: function(){
    	var grid = Ext.getCmp('grid');
		var records = grid.selModel.getSelection();
		if(records.length > 0){
			var id = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
			});
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'common/vastDelete.action',
		   		params: {
		   			caller: caller,
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "删除成功!", function(){
		   					window.location.href = window.location.href;
		   				});
		   			}
		   		}
			});
		}
    }
});