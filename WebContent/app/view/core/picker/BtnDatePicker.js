Ext.define("erp.view.core.picker.BtnDatePicker", {  
    extend: "Ext.picker.Date",  
    alias: "widget.btndatepicker",  
    width: 220,  
    renderTpl: [
    			'<div class="{cls}" id="{id}" role="grid">',
    			'<div class="{baseCls}-header" style="background:#E0E0E0!important;" id ="{id}-headbutton"></div>',
    			'<div role="presentation" class="{baseCls}-header">',
    			'<div class="{baseCls}-prev"><a id="{id}-prevEl" href="#" role="button" title="{prevText}"></a></div>',
    			'<div class="{baseCls}-month" id="{id}-middleBtnEl"></div>',
    			'<div class="{baseCls}-next"><a id="{id}-nextEl" href="#" role="button" title="{nextText}"></a></div>',
    			"</div>",
    			'<table id="{id}-eventEl" class="{baseCls}-inner" cellspacing="0" role="presentation">',
    			'<thead role="presentation"><tr role="presentation">',
    			'<tpl for="dayNames">',
    			'<th role="columnheader" title="{.}"><span>{.:this.firstInitial}</span></th>',
    			"</tpl>",
    			"</tr></thead>",
    			'<tbody role="presentation"><tr role="presentation">',
    			'<tpl for="days">',
    			"{#:this.isEndOfWeek}",
    			'<td role="gridcell" id="{[Ext.id()]}">',
    			'<a role="presentation" href="#" hidefocus="on" class="{parent.baseCls}-date" tabIndex="1">',
    			'<em role="presentation"><span role="presentation"></span></em>',
    			"</a>",
    			"</td>",
    			"</tpl>",
    			"</tr></tbody>",
    			"</table>",
    			'<tpl if="showToday">',
    			'<div id="{id}-footerEl" role="presentation" class="{baseCls}-footer"></div>',
    			"</tpl>", "</div>", {
    				firstInitial : function(a) {
    					return a.substr(0, 1)
    				},
    				isEndOfWeek : function(b) {
    					b--;
    					var a = b % 7 === 0 && b !== 0;
    					return a ? '</tr><tr role="row">' : ""
    				},
    				longDay : function(a) {
    					return Ext.Date.format(a, this.longDayFormat)
    				}
    			}
    ],
    isFormat:'',
    arr:['eq','startWith','endWith','between'],
    css:{
		'margin-left':0,
		'margin-right':0,
		'border':'1px solid #909090',
		'border-radius':0,
		'border-right':'thick'
	},
    focusOnShow:true,
    onRender : function(b, a) {
    	var date = new Date();
		var d = this, e = new Array(d.numDays), c = Ext.Date.format(date,
				d.format);
		var thisMonth = Ext.Date.format(date,'Y-m');
		var thisYear = date.getFullYear();
		var lastMonth = d.getlastMonth();
		var lastYear = date.getFullYear()-1;
		Ext.applyIf(d, {
					renderData : {}
				});
		Ext.apply(d.renderData, {
					dayNames : d.dayNames,
					ariaTitle : d.ariaTitle,
					value : d.value,
					showToday : d.showToday,
					prevText : d.prevText,
					nextText : d.nextText,
					days : e
				});
		d.getTpl("renderTpl").longDayFormat = d.longDayFormat;
		d.addChildEls("eventEl", "prevEl", "nextEl", "middleBtnEl", "footerEl");
		this.callParent(arguments);
		d.el.unselectable();
		d.cells = d.eventEl.select("tbody td");
		d.textNodes = d.eventEl.query("tbody td span");
		d.monthBtn = Ext.create("Ext.button.Split", {
					text : "",
					tooltip : d.monthYearText,
					renderTo : d.middleBtnEl,
					listeners : {
						click:function(){
							d.footerEl.setHeight(0);
							d.el.prev().setHeight(d.el.prev().getHeight()-31);
						}
					}
				});
		d.todayBtn.el.dom.style['margin-left']=0;
		d.todayBtn.el.dom.style['margin-right']=0;
		d.todayBtn.el.dom.style['border']='1px solid #909090';
		d.todayBtn.el.dom.style['border-radius']=0;
		d.todayBtn.el.dom.style['border-right']='thick';
		d.thisMonthBtn = Ext.create("Ext.button.Button", {
					renderTo : d.footerEl,
					text : '本月',
					tooltip : thisMonth,
					style:d.css,
					handler : function(a){
						if (a && !a.disabled) {
							d.isFormat = 'Y-m';
							d.setValue(Ext.Date.clearTime(new Date(thisMonth)));
							d.fireEvent("select", d, d.value);
						}
					},
					scope : d
				});
		d.lastMonthBtn = Ext.create("Ext.button.Button", {
			renderTo : d.footerEl,
			text : '上月',
			style:d.css,
			tooltip : lastMonth,
			handler : function(a){
				if (a && !a.disabled) {
					d.isFormat = 'Y-m';
					d.setValue(Ext.Date.clearTime(new Date(lastMonth)));
					d.fireEvent("select", d, Ext.Date.clearTime(new Date(lastMonth)));
				}
			},
			scope : d
		});
		d.thisYearBtn = Ext.create("Ext.button.Button", {
			renderTo : d.footerEl,
			text : '本年',
			style:d.css,
			tooltip : thisYear,
			handler : function(a){
				if (a && !a.disabled) {
					d.isFormat = 'Y';
					d.setValue(Ext.Date.clearTime(new Date()));
					d.fireEvent("select", d, d.value);
				}
			},
			scope : d
		});
		d.lastYearBtn = Ext.create("Ext.button.Button", {
			renderTo : d.footerEl,
			text : '上年',
			style:{
				'margin-left':0,
				'margin-right':0,
				'border':'1px solid #909090',
				'border-radius':0
			},
			tooltip : lastYear,
			handler : function(a){
				if (a && !a.disabled) {
					d.isFormat = 'Y';
					d.setValue(Ext.Date.clearTime(new Date(lastYear+'-12')));
					d.fireEvent("select", d, d.value);
				}
			},
			scope : d
		});
		d.eqBtn = Ext.create("Ext.button.Button", {
			renderTo : document.getElementById(d.id+'-headbutton'),
			text : '等于',
			id:d.dataIndex+'eq',
			tooltip : '等于',
			style:{
				'background':'#A0A0A0',
				'margin-top':'3px',
				'margin-left':'15px',
				'margin-right':0,
				'border':'1px solid #909090',
				'border-radius':0,
				'border-right':'thick'
			},
			handler : function(a){
				a.el.dom.style.background = '#A0A0A0';
				d.changeBtn(a,'eq');
				d.fireEvent('clickHeadBtn','eq');
			},
			scope : d
		});
		d.startBtn = Ext.create("Ext.button.Button", {
			renderTo : document.getElementById(d.id+'-headbutton'),
			text : '开始于',
			id:d.dataIndex+'startWith',
			tooltip : '开始于',
			style:{
				'margin-left':0,
				'margin-top':'3px',
				'margin-right':0,
				'border':'1px solid #909090',
				'border-radius':0,
				'border-right':'thick'
			},
			handler : function(a){
				a.el.dom.style.background = '#A0A0A0';
				d.changeBtn(a,'startWith');
				d.fireEvent('clickHeadBtn','startWith');
			},
			scope : d
		});
		d.endBtn = Ext.create("Ext.button.Button", {
			renderTo : document.getElementById(d.id+'-headbutton'),
			text : '结束于',
			id:d.dataIndex+'endWith',
			tooltip : '结束于',
			style:{
				'margin-left':0,
				'margin-top':'3px',
				'margin-right':0,
				'border':'1px solid #909090',
				'border-radius':0,
				'border-right':'thick'
			},
			handler : function(a){
				a.el.dom.style.background = '#A0A0A0';
				d.endWith = true;
				d.changeBtn(a,'endWith');
				d.fireEvent('clickHeadBtn','endWith');
			},
			scope : d
		});
		d.betweenBtn = Ext.create("Ext.button.Button", {
			renderTo : document.getElementById(d.id+'-headbutton'),
			text : '介于',
			id:d.dataIndex+'between',
			tooltip : '介于',
			style:{
				'margin-left':0,
				'margin-top':'3px',
				'margin-right':0,
				'border':'1px solid #909090',
				'border-radius':0
			},
			handler : function(a){
				a.el.dom.style.background = '#A0A0A0';
				d.between = true;
				d.changeBtn(a);
				d.fireEvent('clickHeadBtn','between');
			},
			scope : d
		});
		d.on('clickHeadBtn',function(){});
	},
	getlastMonth:function(){
		var date = new Date();
		var month = date.getMonth();
		var year = date.getFullYear();
		if(month == 0){
			return (year-1)+'-12';
		}else if(month < 10){
			return year+'-0'+month;
		}else{
			return year+'-'+month;
		}
	},
	//改变button样式,以达到切换的视觉效果
	changeBtn: function(a,type){
		this.datetype = type;
		for(var i in this.arr){
			var id = this.dataIndex+this.arr[i];
			if(id!=a.id){
				Ext.getDom(id).style.background = '';
			}
		}
	}
});  