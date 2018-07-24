/**
 * 设置工作台的window
 */
Ext.define('erp.view.core.window.WorkBenchSet', {
	extend: 'Ext.window.Window',
	alias: 'widget.workbenchset',
	id : 'win',
	title: '<font color=#CD6839>工作台设置</font>',
	iconCls: 'x-button-icon-set',
	height: screen.height*0.8,
	width: screen.width*0.9,
    maximizable : true,
	buttonAlign : 'center',
	layout : 'border',
	initComponent: function() {
		//工作台所在页面
		this.contentWindow = Ext.getCmp("content-panel").items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow;
		//工作台
		this.bench = this.contentWindow.Ext.getCmp("bench");
		this.setDataStore();
		this.setDataView();
		this.items[1].store = this.store;//grid和DataView用的同一个store，在grid的数据改变时，DataView也会相应的改变
		this.items[2].store = this.store;
		this.callParent(arguments);
		this.show();
	},
	items: [{
		region: 'north',
    	labelAlign: 'right',
    	xtype: 'form',
        frame:true,
        buttonAlign: 'center',
        buttons: [{
        	text: '确  定',
        	handler: function(btn) {
        		var me = Ext.getCmp('win');
        		me.setBench();
        		me.contentWindow.location.reload();
        		me.close();
            }
        },{
            text: '取  消',
            handler: function(btn) {
            	Ext.getCmp('win').close();
            }
        },{
	        text: '应  用',
	        handler: function(btn) {
	        	var me = Ext.getCmp('win');
        		me.setBench();
        		me.contentWindow.location.reload();
        		me.close();
	        }
        }]
	},{
		region: 'west',
		xtype: 'gridpanel',
		id: 'benchdetail',
		width: 400,
		bodyStyle: 'background:#f1f1f1;',
		autoScroll: true,
		enableLocking: true,
	    columns: [
	        { header: '应用', dataIndex: 'wb_isuse', flex: 0.38 ,  xtype: 'checkcolumn',editor: {xtype: 'checkbox',cls: 'x-grid-checkheader-editor'}},
	        { header: '名称', dataIndex: 'wb_detail', flex: 1 },
	        { header: '模块代号', dataIndex: 'wb_name', flex: 0, hidden: true },
	        { header: '序号', dataIndex: 'wb_detno', flex: 0.5, xtype: 'numbercolumn', format: '0',editor: {xtype: 'combobox', editable: false, displayField: 'display',valueField: 'value', queryMode: 'local', store: Ext.create('Ext.data.Store', { fields: ['display', 'value'], data : [{"display": 1, "value": 1},{"display": 2, "value": 2}, {"display": 3, "value": 3}, {"display": 4, "value": 4}, {"display": 5, "value": 5}, {"display": 6, "value": 6}, {"display": 7, "value": 7}, {"display": 8, "value": 8}, {"display": 9, "value": 9}, {"display": 10, "value": 10}, {"display": 11, "value": 11},{"display": 12, "value": 12}]}),listeners:{blur:function(f, sel){
	        	var v = f.value;
	        	var selected = Ext.getCmp('benchdetail').selModel.lastSelected;
	        	var lastValue = selected.data.wb_lastdetno;//f.lastValue没用
	        	Ext.each(Ext.getCmp('benchdetail').store.data.items, function(item, i){
	        		if(item.data.wb_name !=  selected.data.wb_name && item.data.wb_detno == v){
	        			item.set('wb_detno', lastValue);
	        		}
	        	});
	        	selected.set('wb_lastdetno', v);
	        	selected.set('wb_detno', v);
	        	Ext.getCmp('benchdetail').columns[3].doSort('ASC');
	        }}}},
	        { header: '宽度(%)', dataIndex: 'wb_width', flex: 0.5, xtype: 'numbercolumn', editor: {xtype: 'combobox', editable: false, displayField: 'display',valueField: 'value', queryMode: 'local', store: Ext.create('Ext.data.Store', { fields: ['display', 'value'], data : [{"display": 33.33, "value": 33.33},{"display": 66.67, "value": 66.67}]})}},
	        { header: '高度(%)', dataIndex: 'wb_height', flex: 0.5, xtype: 'numbercolumn', editor: {xtype: 'combobox', editable: false, displayField: 'display',valueField: 'value', queryMode: 'local', store: Ext.create('Ext.data.Store', { fields: ['display', 'value'], data : [{"display": 33.33, "value": 33.33},{"display": 66.67, "value": 66.67}]})}},
	    ],
	    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToEdit: 1
		}),
		listeners: {
			afterrender: function(){
				Ext.each(this.store.data.items, function(item, i){
					item.index == i;
	        	});
			}
		}
	}],
	setDataView: function(){
		this.items[2] = Ext.create('Ext.view.View', {
		 	region: 'center',
    		bodyStyle: 'background:#f1f1f1;',
	        deferInitialRefresh: false,
	        enableDragDrop: true,
	        tpl: Ext.create('Ext.XTemplate',
	            '<tpl for=".">',
	            	'<tpl if="wb_isuse">',
		                '<div class="phone" style="display:inline;width:{[values.wb_width > 60 ? 60 : 30]}%;height:{[values.wb_height > 60 ? 60 : 30]}%; border-bottom-style: solid;border-bottom-color: red;border-bottom: medium;">',
		                	'<Strong>{wb_detno}:{wb_detail}</Strong><span>({wb_width}%, {wb_height}%)</span>',
		                    (!Ext.isIE6? '<img src="' + basePath + 'resource/images/icon/{wb_name}.png" title="{wb_detno}:{wb_detail}({wb_width}%, {wb_height}%)" style="display:inline;" width=80;height=80;/>' :
		                     '<div style="width:74px;height:74px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'' + basePath + 'resource/images/icon/{wb_name}.png\',sizingMethod=\'scale\')"></div>'),
		                 '</div>',
	                 '</tpl>',
	                 '<tpl if="wb_isuse == false">',
		                '<div class="phone" style="display:none;width:{[values.wb_width > 60 ? 60 : 30]}%;height:{[values.wb_height > 60 ? 60 : 30]}%; border-bottom-style: solid;border-bottom-color: red;border-bottom: medium;">',
		                	'<Strong>{wb_detno}:{wb_detail}</Strong><span>({wb_width}%, {wb_height}%)</span>',
		                    (!Ext.isIE6? '<img src="' + basePath + 'resource/images/icon/{wb_name}.png" title="{wb_detno}:{wb_detail}({wb_width}%, {wb_height}%)" style="display:inline;" width=80;height=80;/>' :
		                     '<div style="width:74px;height:74px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'' + basePath + 'resource/images/icon/{wb_name}.png\',sizingMethod=\'scale\')"></div>'),
		                 '</div>',
	                 '</tpl>',
	            '</tpl>'
	        ),
	        id: 'phones',
	        style: 'background:#f1f1f1;',
	        itemSelector: 'div.phone',
	        overItemCls : 'phone-hover',
	        multiSelect : true,
	        autoScroll  : true
		});
	},
	setDataStore: function(){
		var me = this;
		var data = me.module;
		var checked = Ext.Array.pluck(me.bench.items.items, 'id');
		var detno = 1;
		Ext.each(data, function(item){
			 item.wb_detno = detno++;
			 item.wb_lastdetno = item.wb_detno;
			 if(Ext.Array.contains(checked, item.wb_name)){
				 var panel = me.bench.down('#' + item.wb_name);
				 if(panel){
					 item.wb_width = Ext.util.Format.number(panel.columnWidth*100, '0.00');
					 item.wb_height = Ext.util.Format.number((panel.height)/me.contentWindow.height*100, '0.00');
					 item.wb_isuse = true;
				 }
			 } else {
				 item.wb_width = 33.33;
				 item.wb_height = 33.33;
				 item.wb_isuse = false;
			 }
		 });
		Ext.define('WorkBench', {
	        extend: 'Ext.data.Model',
	        fields: [
	            {name: 'wb_detail', type: 'string'},
	            {name: 'wb_detno', type: 'int'},
	            {name: 'wb_lastdetno', type: 'int'},
	            {name: 'wb_isuse', type: 'bool'},
	            {name: 'wb_name', type: 'string'},
	            {name: 'wb_width', type: 'string'},
	            {name: 'wb_height', type: 'string'}
	        ]
		});
		me.store = Ext.create('Ext.data.Store', {
			model: 'WorkBench',
			sortInfo: {
				field: 'wb_detno',
				direction: 'ASC'
			},
			data: data
		});
	},
	module: [{ wb_detail: '待办事宜', wb_name: 'bench_task'},
	         	{ wb_detail: '常用模块', wb_name: 'bench_link'},
                { wb_detail: '通知公告', wb_name: 'bench_notify'},
                { wb_detail: '新闻动态', wb_name: 'bench_news'},
                { wb_detail: '日程安排', wb_name: 'bench_subscription'},
                { wb_detail: '我的考勤', wb_name: 'bench_schedule'},
                { wb_detail: '我的知会', wb_name: 'bench_note'},
                { wb_detail: '我的任务', wb_name: 'bench_mytask'},
                { wb_detail: '工作计划', wb_name: 'bench_plan'},
                { wb_detail: '我的邮箱', wb_name: 'bench_email'},
                { wb_detail: '知识地图', wb_name: 'bench_knowledge'},
                { wb_detail: '待开会议', wb_name: 'bench_meeting'},
                { wb_detail: '我的任务流程', wb_name: 'bench_flow'},
                { wb_detail: '我的超时流程', wb_name: 'bench_overflow'}
    ],
    setBench: function(){
    	var data = new Array();
    	Ext.each(this.store.data.items, function(item){
    		if(item.dirty){
    			data.push(item.data);
    		}
    	});
    	Ext.Ajax.request({
        	url : basePath + 'common/setWorkBench.action',
        	method : 'post',
        	params: {
        		data: Ext.encode(data)
        	},
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo) {
        			showError(res.exceptionInfo);
        		}
        	}
		});
    }
});