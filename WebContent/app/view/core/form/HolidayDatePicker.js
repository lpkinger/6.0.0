/**
 * picker,能显示假日
 */
Ext.define('erp.view.core.form.HolidayDatePicker', {
    extend: 'Ext.picker.Date',
    alias: 'widget.holidaydatepicker',
    initComponent : function(){
    	this.callParent(arguments);
    },
    showHolidays: true,//是否显示假日
    showSolarTerm: false,//是否显示24节气
    showLunar: false,//是否显示农历
    update : function(a, d) {
		var b = this, c = b.activeDate;
		if (b.rendered) {
			b.activeDate = a;
			if (!d && c && b.el && c.getMonth() == a.getMonth()
					&& c.getFullYear() == a.getFullYear()) {
				b.selectedUpdate(a, c);
			} else {
				b.fullUpdate(a, c);
			}
			if(b.showHolidays){
				b.setHolodays();
			}
			if(b.showSolarTerm){
				b.setSolarTerm();
			}
			if(b.showLunar){
				b.setLunar();
			}
		}
		return b;
	},
	/**
	 * 显示假日
	 */
	setHolodays: function(){
		var b = this, c = b.activeDate;
		var nodes = b.textNodes;
		var bool = 'prev';
		Ext.each(nodes, function(node){
			var d = node.innerHTML;
			var m = c.getMonth() + 1;
			if(d.length == 1){
				d = '0' + d;
			}
			if(bool == 'prev'){
				if(Number(d) == 1){
					bool = 'curr';
				} else {
					m = m - 1;
				}
			} else if(bool == 'curr'){
				if(Number(d) == 1){
					bool = 'next';
					m = m + 1;
				}
			} else {
				m = m + 1;
			}
			m = m < 10 ? '0' + m : '' + m;
			if(b.holidays[m + d]){
				node.innerHTML = b.holidays[m + d];
			}
		});
	},
	holidays: {'0101': '元旦', '0214': '情人节', '0308': '妇女节', '0312': '植树节', '0401': '愚人节', '0501': '劳动节', '0504': '青年节', 
		'0601': '儿童节', '0910': '教师节', '1001': '国庆节', '1128': '感恩节', '1225': '圣诞节'},
	/**
	 * 显示24节气
	 */
	setSolarTerm: function(){
		
	},
	/**
	 * 显示农历
	 */
	setLunar: function(){
		
	},
	lunarMonths: {
		'01': '正', '02': '二', '03': '三', '04': '四', '05': '五', '06': '六',
		'07': '七', '08': '八', '09': '九', '10': '十', '11': '十一', '12': '腊'
	},
	lunarDays: {
		'01': '初一', '02': '初二', '03': '初三', '04': '初四', '05': '初五', '06': '初六', '07': '初七', '08': '初八', '09': '初九', 
		'10': '初十', '11': '十一', '12': '十二', '13': '十三', '14': '十四', '15': '十五', '16': '十六', '17': '十七', '18': '十八', 
		'19': '十九', '20': '二十', '21': '廿一', '22': '廿二', '23': '廿三', '24': '廿四', '25': '廿五', '26': '廿六', '27': '廿七', 
		'28': '廿八', '29': '廿九', '30': '三十' 
	}
});