Ext.define('erp.view.common.bench.BenchFlowChartWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.benchFlowChart',
	id : 'benchflowchart',
	title: '<font color=#CD6839>业务流程图</font>',
	iconCls: 'x-button-icon-set',
	height: '85%',
	width: '80%',
	modal: true,
    maximizable : true,
	layout: 'border',
	initComponent: function() {
		var me = this;
		Ext.apply(me,{
			items: [{
				xtype: 'panel',
				id: 'flowChartPanel',
				region: 'center',
				layout: 'fit',
				items: []
			}]
		});
		me.callParent(arguments);
		me.show();
	},
	listeners: {
		afterrender: function() {
			var me = this;
			me.getFlowChartConfig();
		}
	},
	getFlowChartConfig: function() {
		var me = this;
		/*var tabs = me.groupSprites([
		{tabName:'面积',type:'area',points: [[175,50],[100,100],[250,100],[175,150]],color:'red'},
		{tabName:'面积',fontSize:18,type:'node',x:200,y:200,width:100,height:40,text:'控诉是',color:'white',bgColor:'blue'},
		{tabName:'带文字线条',type:'line',x:240,y:145,x2:265,y2:345,a1: 1,a2: 1,fontSize: 16,color: 'blue',text: '33'},
		{tabName:'菱形',type:'diamond',points: [[175,150],[100,100], [450,100], [175,150]],text: '3333',x: 100,y: 100,width: 200,bgColor:'#ec753e',height: 90,color: 'red'},
		{tabName:'菱形',type:'line',x:240,y:145,x2:265,y2:345,a1: 1,a2: 1,fontSize: 16,color: 'blue',text: '好请问'},
		{tabName:'空心矩形',type:'node2',x:200,y:200,width:100,height:40,text:'控诉是',color:'red',bgColor:'blue'},
		{tabName:'空心矩形',type:'node',x:200,y:100,width:100,height:40,text:'控诉是',color:'red',bgColor:'blue'}
		]);
		me.createFlowChart(tabs);
		return;*/
		me.getEl().mask('loading');
		Ext.Ajax.request({
			url : basePath + 'bench/getFlowchartConfig.action',
			params: {bccode: me.benchId},
			method : 'post',
			callback : function(options, success, response){
				me.getEl().unmask();
				var res = Ext.decode(response.responseText);
				var sprites = [];
				Ext.Array.each(res.data, function(d) {
					var sprite = {
						benchId: d.BC_ID,
						tabName: d.TABNAME_,
						type: d.TYPE_,
						text: d.TEXT_,
						x: d.X_,
						y: d.Y_,
						width: d.WIDTH_,
						height: d.HEIGHT_,
						items: d.ITEMS_,
						x2: d.X2_,
						y2: d.Y2_,
						a1: d.ARROW1_,
						a2: d.ARROW2_,
						bgColor: d.BG_COLOR,
						color: d.COLOR_,
						fontSize: d.FONT_SIZE,
						dot: d.DOT_,
						points: d.POINTS_
					};
					sprites.push(sprite);
				});
				me.createFlowChart(me.groupSprites(sprites));
			}
		});
	},
	createFlowChart: function(tabs) {
		var me = this,
			panel = Ext.getCmp('flowChartPanel');
			
		if(tabs.length <= 1) {
			var flowChart = {
				xtype: 'flowChart',
	            items: [],
	            listeners: {
	            	afterrender: function() {
	            		this.add(tabs[0] ? me.sortSprites(tabs[0].items) : []);
	            	}
	            }
			};
			panel.add(flowChart);
		}else {
			var items = [];
			Ext.Array.each(tabs, function(t, i) {
				var flowChart = {
					xtype: 'flowChart',
					title: t.tabName || '选项卡'+(i+1),
		            items: [],
		            listeners: {
		            	afterrender: function() {
		            		this.add(me.sortSprites(t.items));
		            	}
		            }
				};
				items.push(flowChart);
			});
			var tabPanel = {
				xtype: 'tabpanel',
				items: items
			}
			panel.add(tabPanel);
		}
	},
	/** 
	 * 将元素按照tabName分组，以显示到不同的页签
	 */
	groupSprites: function(sprites) {
		var group = [],
			groupName = [];
			
		Ext.Array.each(sprites, function(s) {
			s.tabName = s.tabName || '';
			if(groupName.indexOf(s.tabName) == -1) {
				groupName.push(s.tabName);
				group.push({
					tabName: s.tabName,
					items: []
				})
			}
		});
		Ext.Array.each(sprites, function(s) {
			s.tabName = s.tabName || '';
			Ext.Array.each(group, function(g) {
				if(g.tabName == s.tabName) {
					g.items.push(s);
				}
			});
		});
		return group;
	},
	/** 
	 * 因为svg元素渲染时会先后覆盖，所以需要把关键的元素排在后面
	 */
	sortSprites: function(sprites) {
		// 优先级从高到低
		var sortRule = ['rect', 'node', 'area', 'diamond', 'line', 'text'];
		sprites.sort(function(a, b) {
			return sortRule.indexOf(a.type) - sortRule.indexOf(b.type)
		});
		return sprites;
	}
});