package tddtrainer.tracking;

import static org.junit.Assert.*;

import org.junit.Test;

import tddtrainer.catalog.Exercise;

public class SnapshotTest {

	@Test
	public void testThatSnapshotIsImmutable1() {
		Exercise e = new Exercise("test", "xxx");
		Snapshot snapshot = new Snapshot(e, null, null);
		e.setName("lol");
		assertEquals("test", snapshot.exercise.getName());
	}

}
