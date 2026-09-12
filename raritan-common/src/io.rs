//! Big-endian scalar readers shared by the RFB pump.

use eyre::Result;
use std::io::Read;

pub fn read_u8<R: Read>(reader: &mut R) -> Result<u8> {
    let mut value = [0; 1];
    reader.read_exact(&mut value)?;
    Ok(value[0])
}

pub fn read_u16<R: Read>(reader: &mut R) -> Result<u16> {
    let mut value = [0; 2];
    reader.read_exact(&mut value)?;
    Ok(u16::from_be_bytes(value))
}

pub fn read_u32<R: Read>(reader: &mut R) -> Result<u32> {
    let mut value = [0; 4];
    reader.read_exact(&mut value)?;
    Ok(u32::from_be_bytes(value))
}

pub fn read_i32<R: Read>(reader: &mut R) -> Result<i32> {
    let mut value = [0; 4];
    reader.read_exact(&mut value)?;
    Ok(i32::from_be_bytes(value))
}
