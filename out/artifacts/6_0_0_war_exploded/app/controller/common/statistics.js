Ext.QuickTips.init();
Ext.define('erp.controller.common.statistics', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'common.statistics', 'core.form.ConDateField', 'core.button.Query',
            'core.form.ConMonthDateField', 'core.form.YearDateField', 'core.trigger.CateTreeDbfindTrigger'],
    init:function(){
    	var me = this;
    	var condition = getUrlParam('condition');
		if (condition == null) {
			me.createStaWin();	
		} else {
			var value1=getUrlParam('value1');
			var value2=getUrlParam('value2');
			var labels=getUrlParam('labels');
			var str=getUrlParam('fillText');
			str=str.split(","); 
			var fillText="";
		    for (i=0;i<str.length ;i++ )   
		    {   
		    	fillText+=str[i]+'\n';   
		    } 
			var  data = [ {
				name : '直通率',
				value : condition,
				color : 'green'
			}, {
				name : '不良率',
				value : (1-condition),
				color : 'red'
			} ];
			var chart1 = new iChart.Pie2D({
				render : 'canvasDiv1',
				data : data,
				title : {
					text : '工作中心直通率',
					color : '#3e576f'
				},
				sub_option : {
					label : {
						background_color : null,
						sign : false,// 设置禁用label的小图标
						padding : '0 4',
						border : {
							enable : false,
							color : '#666666'
						},
						fontsize : 11,
						fontweight : 600,
						color : '#4572a7'
					},
					border : {
						width : 2,
						color : '#ffffff'
					},
					listeners:{         
	                    click:function(r,e,m){
	                    	
	                    }
	                }
				},
				shadow : true,
				shadow_blur : 6,
				shadow_color : '#aaaaaa',
				shadow_offsetx : 0,
				shadow_offsety : 0,
				background_color : '#E8E8E8',// '#fefefe',
				offsetx : -60,// 设置向x轴负方向偏移位置60px
				offset_angle : -120,// 逆时针偏移120度
				showpercent : true,
				decimalsnum : 2,
				width : 850,
				height : 250,
				radius : 120,
				border : '0px'
			});
			chart1.plugin(new iChart.Custom({
				drawFn:function(){
					//计算位置
					var y = chart1.get('originy'),
						w = chart1.get('width');
					
					//在右侧的位置，渲染说明文字
					chart1.target.textAlign('start').textBaseline('middle').textFont('600 12px Verdana')
					.fillText(fillText,w-220,y-40,false,'black',false,15);
				}
		}));
			var data1 = [ {
				name : '直通率',
				value : eval(value1),
				color : '#4f81bd'
			}, {
				name : '不良率',
				value :eval(value2),
				color : '#bd4d4a'
			} ];

			var chart2 = new iChart.ColumnStacked3D({
				render : 'canvasDiv2',
				data : data1,
				labels :eval(labels),
				title : {
					text:'工序直通率',
					color:'#254d70'
				},
				width : 850,
				height : 300,
				column_width : 90,
				background_color : '#E8E8E8',
				shadow : true,
				shadow_blur : 3,
				shadow_color : '#aaaaaa',
				shadow_offsetx : 1,
				shadow_offsety : 0,
				border : '0px',
				sub_option : {
					label : {
						color : '#f9f9f9',
						fontsize : 12,
						fontweight : 600
					},
					border : {
						width : 2,
						color : '#ffffff'
					}
				},
				label : {
					color : '#254d70',
					fontsize : 12,
					fontweight : 600
				},
				legend : {
					enable : true,
					background_color : null,
					line_height : 25,
					color : '#254d70',
					fontsize : 12,
					fontweight : 600,
					border : {
						enable : false
					}
				},
				tip : {
					enable : true,
					listeners : {
						// tip:提示框对象、name:数据名称、value:数据值、text:当前文本、i:数据点的索引
						parseText : function(tip, name, value, text, i) {
							return name + ":" + value ;
						}
					}
				},
				percent : true,// 标志为百分比堆积图
				showpercent : true,
				decimalsnum : 1,
				text_space : 16,// 坐标系下方的label距离坐标系的距离。
				zScale : 0.5,
				xAngle : 50,
				bottom_scale : 1.1,
				coordinate : {
					width : '82%',
					height : '80%',
					board_deep : 10,// 背面厚度
					pedestal_height : 10,// 底座高度
					left_board : false,// 取消左侧面板
					shadow : true,// 底座的阴影效果
					grid_color : '#6a6a80',// 网格线
					wall_style : [ {// 坐标系的各个面样式
						color : '#6a6a80'
					}, {
						color : '#b2b2d3'
					}, {
						color : '#a6a6cb'
					}, {
						color : '#6a6a80'
					}, {
						color : '#74749b'
					}, {
						color : '#a6a6cb'
					} ],
					axis : {
						color : '#c0d0e0',
						width : 0
					},
					scale : [ {
						position : 'left',
						scale_enable : false,
						start_scale : 0,
						scale_space : 50,
						label : {
							color : '#254d70',
							fontsize : 11,
							fontweight : 600
						}
					} ]
				}
			});

			// 调用绘图方法开始绘图
			chart1.draw();
			chart2.draw();

		}
    	this.control({ 
    		'#statistic': {
    			click: function(btn){
    				var bt = parent.Ext.getCmp('statistic-win');
    				if(!bt){
    					me.createStaWin();	
    				}else{
    					bt.show();
    				}
    			}
    		}
    	});
    },
    createStaWin: function(){
		var column1 = [ {
			labelWidth : 70,
			xtype : 'dbfindtrigger',
			fieldLabel : '工作中心*',
			style: 'color:red',
			allowBlank:false,
			id : 'mc_wccode',
			name : 'mc_wccode'
		} ,
  		 {
			xtype : 'hidden',
			id : 'mc_wcname',
			name : 'mc_wcname'
			} ];
		var column2 = [{
	    	xtype: 'condatefield',
	    	fieldLabel: '日期范围*',
	    	allowBlank: false,
	    	readOnly: true,
	    	style: 'color:red',
	    	labelWidth:70,
	    	id : 'mc_actbegindate',
			name : 'mc_actbegindate',
			width:400
		}];
		var column3=[ {
			labelWidth : 70,
			xtype : 'dbfindtrigger',
			fieldLabel : '线别',
			flex : 0.2,
			id : 'mc_linecode',
			name : 'mc_linecode'
		} ,
  		 {
			xtype : 'hidden',
			id : 'mc_linename',
			name : 'mc_linename'
			} ,{
			labelWidth : 70,
			xtype : 'dbfindtrigger',
			fieldLabel : '产品',
			flex : 0.2,
			id : 'mc_prodcode',
			name : 'mc_prodcode'
		},{
			xtype : 'hidden',
			id : 'mc_prodname',
			name : 'mc_prodname'
			}
			];
		var form = new Ext.form.Panel({
			id:'statistic-form',
			baseCls : "x-plain",
			queryUrl:'pm/statistics.action',
			height : '160px',
			width : '480px',
			maximizable : true,
			buttonAlign : 'center',
			layout : 'column',
			items : [column1, column2,column3],
			buttons : [ {
				text : '确定',
				width : 60,
				cls : 'x-btn-blue',
				handler : function(btn) {
					var form = Ext.getCmp('statistic-form');
					if (form.getForm().isValid()) {
						var params = new Object();
						var r = form.getValues();
						Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
							if(contains(k, 'ext-', true)){
								delete r[k];
							}
						});
						var fillText='工作中心：'+r['mc_wcname']+','+'日期：'+r['mc_actbegindate_from']+'~'+r['mc_actbegindate_to']+',';
						if(r['mc_linename']!=""){
							fillText+='线别：'+r['mc_linename']+',';
						}
						if(r['mc_prodname']!=""){
							fillText+='产品：'+r['mc_prodname'];
						}
						params.param = unescape(escape(Ext.JSON.encode(r)));
						url=form.queryUrl;
						Ext.Ajax.request({
							url : basePath + url,
							params : params,
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								var flag1=(localJson.labels==""||localJson.labels==null)?  true:false ;
								var flag2=(localJson.rate==""||localJson.rate==null)? true:false ;
								var flag3=(localJson.value1==""||localJson.value1==null)? true:false ;
								if(flag1||flag2||flag3){
									alert('没有数据，无法生成图表');
								}else{
									str=window.location.href;
									if(str.indexOf('?')!=-1){
										str=window.location.href;
										str=str.substring(0,str.indexOf('?'));
									}
									window.location.href =str+ '?condition='+ Number(localJson.rate).toFixed(2)+'&labels='+localJson.labels+'&value1='+localJson.value1+'&value2='+localJson.value2+'&fillText='+fillText;								
								}
							}
						});
	    			}
				}
			}, {
				text : '关闭',
				width : 60,
				cls : 'x-btn-blue',
				handler : function(btn) {
					Ext.getCmp('statistic-win').hide();
				}
			} ]
		});
		var win = new Ext.window.Window({
			title : '筛选条件',
			id : 'statistic-win',
			items:form
		});
		win.show();
    }
});