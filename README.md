# PolyMc Extra

## Description

This is a better pre-library for the Polymer patching framework. You can choose to patch manually or use its automatic
configuration. It supports automatic patching of most blocks by default.

I apologize that I haven't had time to compile development documentation. I hope you can read the framework's code to
explore how it is used.

Many thanks to TheEpicBlock_TEB、Patbox and DrexHD for their code help.

## Config: `/config/polymc-extra.json`

``` 
{
	"DisabledOpaqueBlocks": [],
	"CustomBlockModelTypeMappings": {
		"citymod:stone_table": "BLOCK_DISPLAY_ENTITY",
		"citymod:road_block": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_yellow": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_yellow_connect": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_white_connect": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_yellow_double": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_zebra_crossing": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:roadblock_oblique": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:roadblock_yellow_oblique": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:roadblock_double_oblique": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:zebracrossing_oblique": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_straight": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"citymod:road_diamond": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:modern_light": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:computer_mouse": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:black_mirror": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:laptop": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:modern_clock": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:led_floor_lamp": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:shower": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:portable_laptop_stand": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:laptop_closed_portable_laptop_stand": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:cooktop": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:keyboard": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:monitor_setup": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:shower": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:socket": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:modern_chair": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"js_furniture_mod:wood_chair": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"^js_furniture_mod:midi_.+$": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"^js_furniture_mod:.*": "BLOCK_DISPLAY_ENTITY",
		"^skniro_furniture:.*_(drawer|cabinet|fridge|kitchen_sink|oven|tv_stand)$": "BLOCK_DISPLAY_ENTITY",
		"^skniro_furniture:.*_(bed|cushion|sofa|window(?:_style.*)?|plate|chair(?:_.*)?|lamp)$": "BLOCK_DISPLAY_ENTITY_NO_COLLISION",
		"^skniro_furniture:.*_(door)$": "CLOSEABLE_BLOCK",
		"terrestria:cattail": "WATER_PLANT",
		"terrestria:andisol_grass_block": "POLYMER:BIOME_TRANSPARENT_BLOCK"
	},
	"CustomEntityModelMappings": {
		"js_furniture_mod:chair_entity": "polymc-extra:chair",
		"js_furniture_mod:sofa_entity": "polymc-extra:chair",
		"skniro_furniture:chair_entity": "polymc-extra:chair",
		"skniro_furniture:sofa_entity": "polymc-extra:chair",
		"skniro_furniture:cushion_entity": "polymc-extra:chair",
		"citymod:explode_bow_projectile": "minecraft:arrow",
		"citymod:rifle_legacy_projectile": "minecraft:arrow"
	},
	"CustomModelExpansionMappings": {
		"js_furniture_mod:sofa": 0.0,
		"js_furniture_mod:wood_light_table": 0.0,
		"js_furniture_mod:led_floor_lamp_rgb_off": 0.0,
		"js_furniture_mod:led_floor_lamp_rgb_off_2": 0.0,
		"js_furniture_mod:studio_light": 0.0,
		"citymod:road_block": 0.0,
		"citymod:road_yellow": 0.0,
		"citymod:road_yellow_connect": 0.0,
		"citymod:road_white_connect": 0.0,
		"citymod:road_yellow_double": 0.0,
		"citymod:road_zebra_crossing": 0.0
	}
}
```