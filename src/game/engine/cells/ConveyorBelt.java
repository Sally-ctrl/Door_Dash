package game.engine.cells;


public class ConveyorBelt extends TransportCell {

	public ConveyorBelt(String name, int effect) {
		super(name, effect);
	}
	//note i didnt override transport here bc tasks wants it to call with the positive value of effect whic the parent already does
}
